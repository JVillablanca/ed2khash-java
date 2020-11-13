package com.dwarfanvil.utils.ed2khash;

import java.time.LocalDateTime;

/**
 * Interfaz para ser inyectada en {@link com.dwarfanvil.utils.ed2khash.Ed2k#getHashsFromFile(String, Status) getHashsFromFile de la clase Ed2k}
 * @author Juan Villablanca
 * @see com.dwarfanvil.utils.ed2khash.Ed2k
 */
public interface Status {
	/**
	 * Operación invocada periodicamente para informar el avance en la creacion de un proceso de hash.
	 * 
	 * @param fileName Nombre del archivo que se esta procesando
	 * @param totalBytes Cantidad total de bytes a ser procesados
	 * @param bytesProcesados Bytes actualmente procesados
	 * @param fecIni Fecha y hora en que inicio el proceso
	 */
	void update(String fileName,Long totalBytes,Long bytesProcesados,LocalDateTime fecIni);
}

