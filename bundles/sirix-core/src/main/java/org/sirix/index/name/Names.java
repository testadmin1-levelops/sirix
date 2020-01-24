package org.sirix.index.name;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.HashBiMap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import org.sirix.settings.IConstants;

/**
 * Names index structure.
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
public final class Names {

  /** Map the hash of a name to its name. */
  private final Map<Integer, String> mNameMap;

  /** Map which is used to count the occurences of a name mapping. */
  private final Map<Integer, Integer> mCountNameMapping;

  /**
   * Constructor creating a new index structure.
   */
  private Names() {
    mNameMap = new HashMap<>();
    mCountNameMapping = new HashMap<>();
  }

  /**
   * Constructor to build index of from a persistent storage.
   * 
   * @param pIn
   *          the persistent storage
   */
  private Names(final @Nonnull ByteArrayDataInput pIn) {
    final int mapSize = pIn.readInt();
    mNameMap = HashBiMap.create(mapSize);
    mCountNameMapping = new HashMap<>(mapSize);
    for (int i = 0, l = mapSize; i < l; i++) {
      final int key = pIn.readInt();
      final int valSize = pIn.readInt();
      final byte[] bytes = new byte[valSize];
      for (int j = 0; j < bytes.length; j++) {
        bytes[j] = pIn.readByte();
      }
      mNameMap.put(key, new String(bytes));
      mCountNameMapping.put(key, pIn.readInt());
    }
  }

  /**
   * Serialize name-index.
   * 
   * @param pOut
   *          the persistent storage
   */
  public void serialize(final @Nonnull ByteArrayDataOutput pOut) {
    pOut.writeInt(mNameMap.size());
    for (final Entry<Integer, String> entry : mNameMap.entrySet()) {
      pOut.writeInt(entry.getKey());
      final byte[] bytes = getBytes(entry.getValue());
      pOut.writeInt(bytes.length);
      for (final byte byteVal : bytes) {
        pOut.writeByte(byteVal);
      }
      pOut.writeInt(mCountNameMapping.get(entry.getKey()).intValue());
    }
  }

  /**
   * Remove a name.
   * 
   * @param pKey
   *          the key to remove
   */
  public void removeName(final int pKey) {
    final Integer prevValue = mCountNameMapping.get(pKey);
    if (prevValue != null) {
      if (prevValue - 1 == 0) {
        mNameMap.remove(pKey);
        mCountNameMapping.remove(pKey);
      } else {
        mCountNameMapping.put(pKey, prevValue - 1);
      }
    }
  }

  /**
   * Get bytes representation of a string value in a map.
   * 
   * @param pName
   *          the string representation
   * @return byte representation of a string value in a map
   */
  private byte[] getBytes(@Nonnull final String pName) {
    return pName.getBytes(IConstants.DEFAULT_ENCODING);
  }

  /**
   * Create name key given a name.
   * 
   * @param pKey
   *          key for given name
   * @param pName
   *          name to create key for
   */
  public void setName(final int pKey, @Nonnull final String pName) {
    final Integer prevValue = mCountNameMapping.get(pKey);
    if (prevValue == null) {
      mNameMap.put(pKey, checkNotNull(pName));
      mCountNameMapping.put(pKey, 1);
    } else {
      mCountNameMapping.put(pKey, prevValue + 1);
    }
  }

  /**
   * Get the name for the key.
   * 
   * @param pKey
   *          the key to look up
   * @return the string the key maps to
   */
  public String getName(final int pKey) {
    return mNameMap.get(pKey);
  }

  /**
   * Get the number of nodes with the same name.
   * 
   * @param pKey
   *          the key to lookup
   * @return number of nodes with the same name
   */
  public int getCount(final int pKey) {
    Integer names = mCountNameMapping.get(pKey);
    if (names == null) {
      names = 0;
    }
    return names;
  }

  /**
   * Get the name for the key.
   * 
   * @param pKey
   *          the key to look up
   * @return the byte-array representing the string the key maps to
   */
  public byte[] getRawName(final int pKey) {
    return getBytes(mNameMap.get(pKey));
  }

  /**
   * Get a new instance.
   * 
   * @return new instance of {@link Names}
   */
  public static Names getInstance() {
    return new Names();
  }

  /**
   * Clone an instance.
   * 
   * @param pIn
   *          input source, the persistent storage
   * @return cloned index
   */
  public static Names clone(final @Nonnull ByteArrayDataInput pIn) {
    return new Names(pIn);
  }
}
