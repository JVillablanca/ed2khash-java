package com.dwarfanvil.utils.ed2khash;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Juan Villablanca
 * 
 */
public class Ed2k {

	private final static Integer LARGO_CHUNK = 9728000;
	private final static Integer FULL_BLOCK_SIZE=184320;
	private final static Integer LAST_LARGO_BLOCK = 143360;

	private boolean infAvance=false;
	private Status  estado;

	/**
	 * Genera el Hash para el archivo y ademas va entregado el estado de avance.
	 * @param nameFile Ubicacion fisica del archivo
	 * @param informaAvance Clase que implementa la interfaz Status
	 * @return Un Objeto que contiene dieferentes Hashes
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws Ed2kException 
	 */
	public HashsEd2k getHashsFromFile(String nameFile,Status informaAvance) throws IOException, NoSuchAlgorithmException, Ed2kException{
		File f = new File(nameFile);
		return getHashsFromFile(f,informaAvance);
	}


	/**
	 * Genera el Hash para el archivo y ademas va entregado el estado de avance.
	 * @param file Objeto File del archivo
	 * @param informaAvance Clase que implementa la interfaz Status
	 * @return Un Objeto que contiene dieferentes Hashes
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws Ed2kException 
	 */
	public HashsEd2k getHashsFromFile(File file,Status informaAvance) throws IOException, NoSuchAlgorithmException, Ed2kException{
		if(informaAvance!=null){
			this.infAvance=true;
			this.estado=informaAvance;
		}
		return getHashsFromFile(file);
	}	

	/**
	 * @param nameFile Ubicacion fisica del archivo.
	 * @return Un Objeto que contiene dieferentes Hashes
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws Ed2kException 
	 */
	public HashsEd2k getHashsFromFile(String nameFile) throws IOException, NoSuchAlgorithmException, Ed2kException{
		File f = new File(nameFile);
		return getHashsFromFile(f);
	}

	/**
	 * Genera el Hash para el archivo
	 * @param file Objeto File del archivo
	 * @return Un Objeto que contiene dieferentes Hashes
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws Ed2kException 
	 */
	public HashsEd2k getHashsFromFile(File file) throws IOException, NoSuchAlgorithmException, Ed2kException{
		InputStream fis = new FileInputStream(file);
		HashsEd2k hashs=null;
		try {
			hashs = getHashsFromStream(fis, file.getName(), file.length());
		} finally {
			if(fis!=null){
				fis.close();
			}
		}
		return hashs;
	}
	
	public HashsEd2k getHashsFromStream(InputStream is,String fileName,long fileLength ) throws IOException, NoSuchAlgorithmException, Ed2kException{
		LocalDateTime fecini= LocalDateTime.now();
		byte[] dataBytes = new byte[LARGO_CHUNK];
		MD4 md4  = new MD4();
		MD4 md4e = new MD4();
		int nread = 0;
		int lread = 0;
		int ciclo=0;
		byte[] dig=null;
		Map<Integer,byte[]> hashset = new HashMap<Integer,byte[]>();
		long largo=fileLength;
		String nombre=fileName;
		Aich aich=new Aich();
		long bytesProcesados=0;
		while ((nread = is.read(dataBytes)) != -1) {
			aich.update(dataBytes, nread);
			lread=nread;
			md4.engineReset();
			md4.update(dataBytes, 0, nread);
			dig=md4.digest();
			hashset.put(ciclo, dig);
			md4e.update(dig,0,16);
			ciclo++;
			if(this.infAvance){
				bytesProcesados+=nread;
				this.estado.update(nombre, largo, bytesProcesados, fecini);
			}
		}
		if(lread%LARGO_CHUNK==0){
			byte[] nullchunk=new byte[0];
			md4.engineReset();
			md4.update(nullchunk,0,0);
			byte[] nulldigest=md4.digest();
			md4e.update(nulldigest,0,16);
		}
		byte[] d2=md4e.digest();
		byte[] mdbytes;
		if(ciclo>1||lread%LARGO_CHUNK==0) mdbytes= d2; else mdbytes=dig;
		aich.digest();
		byte[][] hashArray=new byte[hashset.size()][];
		for(int i=0;i<hashset.size();i++) hashArray[i]=hashset.get(i);
		HashsEd2k hashs = new HashsEd2k(bytesToHex(mdbytes),aich.getHashAICH(),nombre,largo,hashArray,aich.getHashChunksSet());
		return hashs;
	}

	protected static String bytesToHex(byte[] mdbytes){
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString().toUpperCase();
	}


	/**
	 * Entrega los datos de la ubicacion de los bytes en un bloque dados el numero de bloque aich y el largo del archivo.
	 * @param numBlock numero de bloque
	 * @param largoFile largo en bytes del archivo
	 * @return Objeto con datos de posicion
	 * @throws Ed2kException
	 */
	public static Block getBlockByNum(long numBlock,long largoFile) throws Ed2kException{
		if(numBlock<0||largoFile<0) {throw new Ed2kException("numero negativo");}
		long nchuncks = numBlock/53L;
		long bytesenchunks  = nchuncks * LARGO_CHUNK;
		long blocksenchunks = nchuncks * 53;
		long restoBlocks=numBlock-blocksenchunks;
		long restoBytes=restoBlocks*FULL_BLOCK_SIZE;
		long inicio = bytesenchunks + restoBytes;

		Block b=null;
		if(inicio>=largoFile)
		{
			b = new Block(-1,-1,-1);
		}
		else if(largoFile>=inicio+FULL_BLOCK_SIZE)
		{
			b= new Block(numBlock,inicio,inicio+FULL_BLOCK_SIZE-1);
		}else
		{
			b= new Block(numBlock,inicio,largoFile-1);
		}
		if((numBlock+1)%(float)53==0&&b.getFin()-b.getInicio()>=LAST_LARGO_BLOCK)
		{
			b = new Block(numBlock,inicio,inicio+LAST_LARGO_BLOCK-1);
		}
		
		return b;
	}

}
