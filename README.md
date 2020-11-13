# lib-ed2khash

Libreria java que genera hashs ed2k y AICH.

## DEFINICIONES

### Hash

Una función criptográfica hash- usualmente conocida como “hash”- es un algoritmo matemático que transforma cualquier bloque arbitrario de datos en una nueva serie de caracteres con una longitud fija. Independientemente de la longitud de los datos de entrada, el valor hash de salida tendrá siempre la misma longitud.

### ED2K
En informática, un enlace eD2k (ed2k://) es un hiperenlace usado para localizar archivos dentro de la red peer-to-peer eDonkey.1​ Este tipo de enlaces hacen referencia a un archivo de forma unívoca y pueden compartirse a través de páginas web o cualquier otro medio. Originalmente fue implementado en el cliente eDonkey2000, pero ha sido adoptado por la mayoría de los programas capaces de conectarse a la red eDonkey como eMule, aMule, Shareaza o MLDonkey entre otros.

Los enlaces eD2k fueron uno de los primeros URIs introducidos en las redes peer-to-peer y esto tuvo un gran efecto en el desarrollo de la red eDonkey porque permitía que páginas web externas proporcionaran contenidos verificados dentro de la red. Hoy en día los enlaces magnet están sustituyendo a los enlaces eD2k. Estos tienen un papel similar, pero no están limitados solo a la red eDonkey, sino que también pueden ser usados en otras redes como por ejemplo la red BitTorrent.

Estos enlaces usan un **hash criptográfico** para identificar de forma unívoca los archivos dentro de la red. Esto permite que aunque un archivo tenga diferentes nombres en distintos ordenadores de la red, pueda ser descargado de todas las fuentes, por lo tanto no depende del nombre de los archivos sino de su contenido.

Ejemplo:

    ed2k://|file|eMule0.50a.exe|3389035|3D366ED505B977FC61C9A6EE01E96329|h=EKE4PSKRQ65MWEPFTRDSAHW5VMDIMFAJ|/

En donde **3D366ED505B977FC61C9A6EE01E96329** es el hash que identifica al archivo, si un bit en el archivo cambiara, este hash cambiara totalmente, por lo que si se puede regenerar el hash aseguramos la integridad de nuestro archivo.
Pero este Hash se forma en base a hash ([MD4](https://es.wikipedia.org/wiki/MD4)) formados con bloques (chunks) de 9.28 MB (9728000 bytes), esto implica que si un byte dentro del bloque de 9.28 MB esta malo, tendremos que recuperar los 9.28 MB nuevamente hasta que podamos generar el hash correcto del chunk y con esto el hash correcto del archivo.

### AICH
AICH (Advanced Intelligent Corruption Handler) En el metodo anterior si el archivo se corrompe solo podremos saber donde se corrompio en un bloque de 9.28 MB, ademas los hash del metodo anterior se crean con MD4 por lo que son vulnerables a [colisiones](https://eprint.iacr.org/2004/199.pdf), por lo que AICH toma los chunks de 9.28 MB y los divide en 53 bloques de 180KB (el ultimo bloque es de solo 140KB) y con estos bloques se crean hash [SHA-1](https://es.wikipedia.org/wiki/Secure_Hash_Algorithm) (que hasta el momento es muy dificil de colisionar), por lo que si se detecta un error se sabra extactmente que bloque de 180KB tiene problemas.

## UTILIDAD

La libreria permite obtener un objeto con todos los hashs de un archivo, ya sea ED2K o AICH, con estos hashs respaldados se podra tener claridad de la integridad de un archivo y del lugar de su corrupción (un bloque de 180KB).

## USO

El algoritmo se puede invocar de varias maneras, la mas simple:
```java
Ed2k e = new Ed2k();
HashsEd2k hashs = e.getHashsFromFile("C:\\hola.txt");
System.out.println("Hash Ed2k ="+hashs.getEd2kHash());
System.out.println("Hash Aich ="+hashs.getAichHash());
```    

Tambien se puede entregar directamente un objeto File

```java
File f = new File("C:\\hola.txt");
Ed2k e = new Ed2k();
HashsEd2k hashs = e.getHashsFromFile(f);
System.out.println("Hash Ed2k ="+hashs.getEd2kHash());
System.out.println("Hash Aich ="+hashs.getAichHash());
```

Si se desea conocer el avance del proceso, se puede inyectar un objeto de tipo status que recibira periodicamente
información para ser desplegada de alguna manera.

Por ejemplo esta clase esta pensada para mostrar una barra de estado en la consola:

```java
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.dwarfanvil.utils.ed2khash.Status;

/**
 * Muestra una barra de progreso en la consola
 * @author Mordekay
 *
 */
public class StatusBarSingle implements Status {

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
	private static final int LARGO_BARRA = 40;
	private static final int MAX_NANO = 999999999;
	public StatusBarSingle() 
	{
	}


	/* (non-Javadoc)
	 * @see com.dwarfanvil.utils.ed2khash.Status#update(java.lang.String, java.lang.Long, java.lang.Long, java.time.LocalDateTime)
	 */
	@Override
	public void update(String fileName, Long totalBytes, Long bytesProcesados, LocalDateTime fecIni) {

		int nanot = Duration.between(fecIni, LocalDateTime.now()).getNano();
		Long st = Duration.between(fecIni, LocalDateTime.now()).getSeconds();
		float avance=bytesProcesados/(float)totalBytes;
		Long tt = 0l;
		if(avance>0){
			tt = (long)(st/avance);
		}
		LocalDateTime nt = fecIni.plusSeconds(tt);
		int nb = (int) (LARGO_BARRA * avance);

		float mbs=0;
		float mb=(float)bytesProcesados/(1024*1024);
		if(st>0){
			mbs=(float)mb/st;
		}
		else
		{
			if(nanot>0)
			{
               mbs=mb*MAX_NANO/nanot;
			}
		}

		System.out.printf(String.format("%6.2f", avance*100).replace(',', '.')+"%% ["+((nb==0)?"":String.format("%"+nb+"s","").replace(' ', '='))+((nb==LARGO_BARRA)?"":String.format("%"+(LARGO_BARRA-nb)+"s",""))+"] "+nt.format(this.formatter)+" "+String.format("%.2f", mbs).replace(',', '.')+" MB/s \r");

		if(bytesProcesados==totalBytes)
		{
			System.out.println("");
		}

	}

} 
```

Luego esta clase se puede usar inyectando el objeto como parametro:

```java
Status avance = new StatusBarSingle();
Ed2k e = new Ed2k();
HashsEd2k hashs = e.getHashsFromFile("C:\\archivomuygrande.dat",avance);
System.out.println("Hash Ed2k ="+hashs.getEd2kHash());
System.out.println("Hash Aich ="+hashs.getAichHash());
```  


  