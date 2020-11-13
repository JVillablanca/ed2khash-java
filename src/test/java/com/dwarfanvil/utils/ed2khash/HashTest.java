package com.dwarfanvil.utils.ed2khash;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import org.testng.annotations.Test;

import com.dwarfanvil.utils.ed2khash.Ed2k;
import com.dwarfanvil.utils.ed2khash.Ed2kException;
import com.dwarfanvil.utils.ed2khash.HashsEd2k;
import com.dwarfanvil.utils.ed2khash.Status;

/**
 * Clase de pruebas
 * @author Mordekay
 *
 */
public class HashTest {

	/**
	 * La prueba consiste en generar el hash para un archivo que se encuentra en la carpeta de recursos
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws Ed2kException 
	 */
	@Test
	public void test01() throws NoSuchAlgorithmException, IOException, Ed2kException{
		Ed2k ed2k = new Ed2k();

		String nameFile="hola.txt"; // tambien podria ser "C:\\misDatos\\miDocumento.doc" pero pasariamos directamente el String
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(URLDecoder.decode(classLoader.getResource(nameFile).getFile(),java.nio.charset.StandardCharsets.UTF_8.toString()));

		Status informaAvance = new Estado();

		HashsEd2k hash = ed2k.getHashsFromFile(file, informaAvance);

		assert hash.getEd2kHash().equalsIgnoreCase("0FBD276737B27032BCAB309292EE54D9"):"Error en hash ed2k";
		assert hash.getAichHash().equalsIgnoreCase("3LCT5WLARPPTIC2VGKBVLVFKKEN2ZP2Z"):"Error en hash aich";
		assert hash.getEd2kLink().equalsIgnoreCase("ed2k://|file|hola.txt|13|0FBD276737B27032BCAB309292EE54D9|h=3LCT5WLARPPTIC2VGKBVLVFKKEN2ZP2Z|/"):"Error en Link ed2k";
		assert hash.toString().equalsIgnoreCase("ed2k://|file|hola.txt|13|0FBD276737B27032BCAB309292EE54D9|h=3LCT5WLARPPTIC2VGKBVLVFKKEN2ZP2Z|/"):"Error en Link ed2k";
		assert hash.getMagnetLink().equalsIgnoreCase("magnet:?xt.1=urn:ed2k:0FBD276737B27032BCAB309292EE54D9&xt.2=urn:aich:3LCT5WLARPPTIC2VGKBVLVFKKEN2ZP2Z&dn=hola.txt&xl=13"):"Error con MagneticLink";
		assert hash.getCV().equalsIgnoreCase("3854745BB7574DA79A8DFA2BA82A6516FAECDADC"):"El codigo de verificacion tiene error";
		assert hash.getCV().equalsIgnoreCase("3854745BB7574DA79A8DFA2BA82A6516FAECDADC"):"El codigo de verificacion tiene error (cache)";
		assert hash.getAichHashsetStr().length==1:"El largo del hashset no es uno";
		assert hash.getAichHashsetStr()[0].equalsIgnoreCase("3LCT5WLARPPTIC2VGKBVLVFKKEN2ZP2Z"):"El primer hashset no coincide.";
		System.out.println();
	}

	@Test
	public void test02() throws NoSuchAlgorithmException, IOException, Ed2kException{
		Ed2k ed2k = new Ed2k();
		byte[] bytesArray = new byte[9728011];
		for(int i=0;i<9728011;i++){
			bytesArray[i]=11;
		}
		InputStream is=new ByteArrayInputStream(bytesArray);
		bytesArray=null;
		HashsEd2k hash = ed2k.getHashsFromStream(is,"Test",9728011L);
		is.close();
		assert hash.getEd2kHash().equalsIgnoreCase("20130BB1D1A80C836F4DABF2DF3373A9"):"Error en hash ed2k ["+hash.getEd2kHash()+"]";
		assert hash.getAichHash().equalsIgnoreCase("USUHF6QI2DF4ZL5Y6BPKCTXFDM2CBQJA"):"Error en hash aich ["+hash.getAichHash()+"]";
		
	}

	@Test
	public void test03() throws NoSuchAlgorithmException, IOException, Ed2kException{
		Ed2k ed2k = new Ed2k();
		byte[] bytesArray = new byte[19456022];
		for(int i=0;i<19456022;i++){
			bytesArray[i]=11;
		}
		InputStream is=new ByteArrayInputStream(bytesArray);
		bytesArray=null;
		HashsEd2k hash = ed2k.getHashsFromStream(is,"Test",19456022L);
		is.close();
		assert hash.getEd2kHash().equalsIgnoreCase("45438F633E639E363A0A0E1C9C0124D0"):"Error en hash ed2k ["+hash.getEd2kHash()+"]";
		assert hash.getAichHash().equalsIgnoreCase("RA7XW74QFONHH6LGYPHM35WXUNGZIU77"):"Error en hash aich ["+hash.getAichHash()+"]";
		
	}	

	@Test
	public void Seg01() throws Ed2kException{
		String[] set1 = {"6STK7AFBBLOXRNED25KZZA2UV4\u003d\u003d\u003d\u003d\u003d\u003d","X27FPHJB6MRVSAHBZLK5ZFDKFI\u003d\u003d\u003d\u003d\u003d\u003d","6BTMUGWXLU5AA6DL4XOUVVMR4Y\u003d\u003d\u003d\u003d\u003d\u003d"};
		String[] set2 = {"C5F6B6G2KF33LDQMFCIH5FFYG4M6TAU3","APKP64QT5B6INQ6SG5WRWUZWNN4R2SPS"};
		HashsEd2k hash = new HashsEd2k("1234ABCD","5678EFGH","prueba.txt",100L,set1,set2);
		assert hash.getAichHashset()[0][0]==23:"El byte de Aich no se convirtio correctamente";
		hash.getAichHashset()[0][0]=99;
		assert hash.getAichHashset()[0][0]==23:"El byte de Aich no se mantuvo";

	}	
	
	
	@Test
	public void hashVO01() throws Ed2kException{
		String[] set1 = {"6STK7AFBBLOXRNED25KZZA2UV4\u003d\u003d\u003d\u003d\u003d\u003d","X27FPHJB6MRVSAHBZLK5ZFDKFI\u003d\u003d\u003d\u003d\u003d\u003d","6BTMUGWXLU5AA6DL4XOUVVMR4Y\u003d\u003d\u003d\u003d\u003d\u003d"};
		String[] set2 = {"C5F6B6G2KF33LDQMFCIH5FFYG4M6TAU3","APKP64QT5B6INQ6SG5WRWUZWNN4R2SPS"};
		HashsEd2k hash = new HashsEd2k("1234ABCD","5678EFGH","prueba.txt",100L,set1,set2);
		assert hash.getEd2kHash().equalsIgnoreCase("1234ABCD"):"No se recupero el hash Ed2k correctamente";
		assert hash.getAichHash().equalsIgnoreCase("5678EFGH"):"No se recupero el hash AICH correctamente";
		assert hash.getNombre().equalsIgnoreCase("prueba.txt"):"No se recupero el nombre del archivo correctamente";
		assert hash.getSize().equals(100L):"No se recupero el largo del archivo correctamente";
		assert hash.getAichHashsetStr().length==2:"El largo del set aich no es correcto";
		assert hash.getEd2kHashsetStr().length==3:"El largo del set ed2k no es correcto";
		assert hash.getAichHashsetStr()[0].equalsIgnoreCase("C5F6B6G2KF33LDQMFCIH5FFYG4M6TAU3"):"Un elemento del set AICH no es correcto";
		assert hash.getEd2kHashsetStr()[2].equalsIgnoreCase("6BTMUGWXLU5AA6DL4XOUVVMR4Y\u003d\u003d\u003d\u003d\u003d\u003d"):"Un elemento del set ED2K no es correcto";
		assert hash.getAichHashset()[0][0]==23:"El byte de Aich no se conviertio correctamente";
		assert hash.getEd2kHashset()[0][0]==-12:"El byte de Ed2k no se conviertio correctamente";
	}


	
	@Test
	public void hashVO02() throws Exception{
		String[] set1 = null;
		String[] set2 = {"C","D"};
		try {
			HashsEd2k hash = new HashsEd2k("1234ABCD","5678EFGH","prueba.txt",100L,set1,set2);
			throw new Exception("Debio levantarse Excepcion");
		} catch (Ed2kException e) {
			assert e.getMessage().equalsIgnoreCase("Parametro nulo");
		}
	}

	@Test
	public void hashVO03() throws Exception{
		String[] set1 = {"A","B","M"};
		String[] set2 = {"C","D"};
		try {
			HashsEd2k hash = new HashsEd2k("1234ABCD","5678EFGH",null,100L,set1,set2);
			throw new Exception("Debio levantarse Excepcion");
		} catch (Ed2kException e) {
			assert e.getMessage().equalsIgnoreCase("Parametro nulo");
		}
	}	

	@Test
	public void hashVO04() throws Exception{
		String[] set1 = {"A","B","M"};
		String[] set2 = {"C","D"};
		try {
			HashsEd2k hash = new HashsEd2k("1234ABCD","5678EFGH","archivo.txt",0L,set1,set2);
			throw new Exception("Debio levantarse Excepcion");
		} catch (Ed2kException e) {
			assert e.getMessage().equalsIgnoreCase("Size<=0");
		}
	}	

	@Test
	public void ExceptionTest(){
		try {
			throw new Ed2kException();
		} catch (Ed2kException e) {
			assert e.getMessage()==null;
		}

		try {
			throw new Ed2kException("TEST");
		} catch (Ed2kException e) {
			assert e.getMessage().equalsIgnoreCase("TEST");
		}
		
		try {
			try {
				throw new Exception("SubException");
			} catch (Exception e) {
				throw new Ed2kException("TEST",e);
			}
		} catch (Ed2kException e) {
			assert e.getMessage().equalsIgnoreCase("TEST");
		}
		
		try {
			try {
				throw new Exception("SubException");
			} catch (Exception e) {
				throw new Ed2kException(e);
			}
		} catch (Ed2kException e) {
			assert e.getMessage().equalsIgnoreCase("java.lang.Exception: SubException");
		}
		
		try {
			try {
				throw new Exception("SubException");
			} catch (Exception e) {
				throw new Ed2kException("TEST",e,true,true);
			}
		} catch (Ed2kException e) {
			assert e.getMessage().equalsIgnoreCase("TEST");
		}


	}

}

