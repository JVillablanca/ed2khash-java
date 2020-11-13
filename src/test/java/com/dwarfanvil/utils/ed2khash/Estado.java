package com.dwarfanvil.utils.ed2khash;

import java.time.LocalDateTime;

import com.dwarfanvil.utils.ed2khash.Status;

public class Estado implements Status {

	@Override
	public void update(String fileName, Long totalBytes, Long bytesProcesados, LocalDateTime fecIni) {
		System.out.println("Procesando archivo:"+fileName);
	}

}
