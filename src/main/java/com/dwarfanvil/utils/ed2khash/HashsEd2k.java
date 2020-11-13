package com.dwarfanvil.utils.ed2khash;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;
import org.apache.commons.codec.binary.Base32;
import com.google.common.net.UrlEscapers;

/**
 * @author Juan Villablanca
 *
 */
public class HashsEd2k implements Serializable{


	private static final long serialVersionUID = -895357343661577459L;
	private String   ed2kHash;
	private String   aichHash;
	private String   nombre;
	private Long     size;
	private byte[][] hashset;
	private byte[][] blockset;
	private String cv;

	@SuppressWarnings("unused")
	private HashsEd2k() {}

	/**
	 * Constructor del Value Object para los hashs, este constructor permite ingresar los sets como String 
	 * @param ed2kHash Hash Ed2k
	 * @param aichHash Hash Aich
	 * @param nombre Nombre del archivo
	 * @param size Tamaño del archivo
	 * @param ed2kHashSet Set de hashs que forman al ed2kHash 
	 * @param aichHashSet Set de hashs que forman al aichHash
	 * @throws Ed2kException Excepcion en caso de problemas con los parametros.
	 */
	public HashsEd2k(String ed2kHash, String aichHash, String nombre, Long size,String[] ed2kHashSet,String[] aichHashSet) throws Ed2kException {
		if(ed2kHashSet==null||aichHashSet==null){
			throw new Ed2kException("Parametro nulo");	
		}
		Base32 base32 = new Base32();
		byte[][] ehsbyte = new byte[ed2kHashSet.length][];
		for(int i=0;i<ed2kHashSet.length;i++){
			ehsbyte[i]=base32.decode(ed2kHashSet[i]);
		}
		byte[][] aichbyte = new byte[aichHashSet.length][];
		for(int i=0;i<aichHashSet.length;i++){
			aichbyte[i]=base32.decode(aichHashSet[i]);
		}
		setValues(ed2kHash,aichHash,nombre,size,ehsbyte,aichbyte);
	}



	/**
	 * Asigna los valores al objeto
	 * @param ed2kHash Hash Ed2k
	 * @param aichHash Hash Aich
	 * @param nombre Nombre del archivo
	 * @param size Tamaño del archivo
	 * @param ed2kHashSet Set de hashs que forman al ed2kHash 
	 * @param aichHashSet Set de hashs que forman al aichHash
	 * @throws Ed2kException Excepcion en caso de problemas con los parametros.
	 */
	private void setValues(String ed2kHash, String aichHash, String nombre, Long size,final byte[][] ed2kHashSet,final byte[][] aichHashSet) throws Ed2kException {
		if(ed2kHash==null||aichHash==null||nombre==null||size==null||ed2kHashSet==null||aichHashSet==null){
			throw new Ed2kException("Parametro nulo");
		}
		if(size<=0){
			throw new Ed2kException("Size<=0");
		}
		this.ed2kHash = ed2kHash.toUpperCase();
		this.aichHash = aichHash.toUpperCase();
		this.nombre = nombre;
		this.size = size;
		this.hashset=ed2kHashSet;
		this.blockset=aichHashSet;
		this.cv=null;
	}

	/**
	 * Constructor del Value Object para los hashs, este constructor permite ingresar los sets como arreglo de bytes 
	 * @param ed2kHash Hash Ed2k
	 * @param aichHash Hash Aich
	 * @param nombre Nombre del archivo
	 * @param size Tamaño del archivo
	 * @param ed2kHashSet Set de hashs que forman al ed2kHash 
	 * @param aichHashSet Set de hashs que forman al aichHash
	 * @throws Ed2kException Excepcion en caso de problemas con los parametros.
	 */
	public HashsEd2k(String ed2kHash, String aichHash, String nombre, Long size,byte[][] ed2kHashSet,byte[][] aichHashSet) throws Ed2kException {
		setValues(ed2kHash,aichHash,nombre,size,ed2kHashSet,aichHashSet);
	}

	public Long getSize() {
		return size;
	}

	public static byte[][] deepCopyByteMatrix(byte[][] input) {
	    if (input == null)
	        return null;
	    byte[][] result = new byte[input.length][];
	    for (int r = 0; r < input.length; r++) {
	        result[r] = input[r].clone();
	    }
	    return result;
	}
	
	public byte[][] getHashset() {
		return deepCopyByteMatrix(hashset);
	}

	public byte[][] getEd2kHashset() {
		return getHashset();
	}

	public byte[][] getAichHashset() {
		return deepCopyByteMatrix(blockset);
	}

	public String[] getEd2kHashsetStr() {
		String[] ehs = new String[hashset.length];
		Base32 base32 = new Base32();
		for(int i=0;i<hashset.length;i++){
			ehs[i]= base32.encodeAsString(hashset[i]);
		}
		return ehs;
	}

	public String[] getAichHashsetStr() {
		String[] bs = new String[blockset.length];
		Base32 base32 = new Base32();
		for(int i=0;i<blockset.length;i++){
			bs[i]= base32.encodeAsString(blockset[i]);
		}
		return bs;
	}	

	public String getNombre() {
		return nombre;
	}

	public String getEd2kHash() {
		return ed2kHash;
	}

	public String getAichHash() {
		return aichHash;
	}

	@Override
	public String toString() {
		return this.getEd2kLink();
	}

	public String toHex(Long input){
		return String.format("%08X", input);
	}


	private byte[] getBytesUtf8(String string) throws Ed2kException{
		
		try {
			return string.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new Ed2kException("El sistema no soporta UTF-8",e);
		}
	}

	/**
	 * Codigo Verificador para verificar los elementos mas importantes<br>
	 * @return string de 8 caracteres
	 * @throws Ed2kException En caso de que el sistema no soporte UTF8
	 */
	public String getCV() throws Ed2kException {
		if(cv!=null){ 
			return cv;
		}
		CRC32 crc32 = new CRC32();
		crc32.reset();
		crc32.update(getBytesUtf8(String.valueOf(this.size)));

		String chksize=toHex(crc32.getValue());
		crc32.reset();
		crc32.update(getBytesUtf8(this.ed2kHash));
		String chked2k = toHex(crc32.getValue());
		crc32.reset();
		crc32.update(getBytesUtf8(this.aichHash));
		String chkaich = toHex(crc32.getValue());
		crc32.reset();
		for(int i=0;i<this.hashset.length;i++) crc32.update(this.hashset[i]);
		String chkseted2k = toHex(crc32.getValue());
		crc32.reset();
		for(int i=0;i<this.blockset.length;i++) crc32.update(this.blockset[i]);
		String chksetaich = toHex(crc32.getValue());
		this.cv = chksize+chked2k+chkaich+chkseted2k+chksetaich;
		return cv;
	}

	public String getEd2kLink(){
		return "ed2k://|file|"+UrlEscapers.urlFragmentEscaper().escape(nombre)+"|"+size+"|"+ed2kHash+"|h="+aichHash+"|/";
	}

	public String getMagnetLink(){
		return "magnet:?xt.1=urn:ed2k:"+ed2kHash+"&xt.2=urn:aich:"+aichHash+"&dn="+UrlEscapers.urlFragmentEscaper().escape(nombre)+"&xl="+size;
	}


}
