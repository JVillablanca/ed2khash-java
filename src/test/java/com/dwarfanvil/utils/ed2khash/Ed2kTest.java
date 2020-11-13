package com.dwarfanvil.utils.ed2khash;

import org.testng.annotations.Test;

import com.dwarfanvil.utils.ed2khash.Block;
import com.dwarfanvil.utils.ed2khash.Ed2k;

public class Ed2kTest {

	@Test
	public void bloque1() throws Exception{
		Block b = Ed2k.getBlockByNum(0, 100);
		assert b.getNumBlock()==0L:"Error en numero de bloque";
		assert b.getInicio()==0L:"Error en byte de inicio";
		assert b.getFin()==99L:"Error en byte de Fin";
	}

	@Test
	public void bloque184320() throws Exception{
		Block b = Ed2k.getBlockByNum(0, 184320);
		assert b.getNumBlock()==0L:"Error en numero de bloque";
		assert b.getInicio()==0L:"Error en byte de inicio";
		assert b.getFin()==184319L:"Error en byte de Fin";
	}
	
	@Test
	public void bloque184321() throws Exception{
		Block b = Ed2k.getBlockByNum(1, 184321);
		assert b.getNumBlock()==1L:"Error en numero de bloque";
		assert b.getInicio()==184320L:"Error en byte de inicio ["+b.getInicio()+"]";
		assert b.getFin()==184320L:"Error en byte de Fin ["+b.getFin()+"]";
	}
	
	@Test
	public void bloque184319() throws Exception{
		Block b = Ed2k.getBlockByNum(0, 184319);
		assert b.getNumBlock()==0L:"Error en numero de bloque";
		assert b.getInicio()==0L:"Error en byte de inicio ["+b.getInicio()+"]";
		assert b.getFin()==184318L:"Error en byte de Fin ["+b.getFin()+"]";
	}
	
	/**
	 * Un Chunk
	 * @throws Exception
	 */
	@Test
	public void bloque9728000() throws Exception{
		Block b = Ed2k.getBlockByNum(52, 9728000);
		assert b.getNumBlock()==52L:"Error en numero de bloque";
		assert b.getInicio()==9584640L:"Error en byte de inicio ["+b.getInicio()+"]";
		assert b.getFin()==9727999L:"Error en byte de Fin ["+b.getFin()+"]";
	}	

	@Test
	public void bloque9728000v2() throws Exception{
		Block b = Ed2k.getBlockByNum(53, 9728000);
		assert b.getNumBlock()==-1L:"Error en numero de bloque";
		assert b.getInicio()==-1L:"Error en byte de inicio ["+b.getInicio()+"]";
		assert b.getFin()==-1L:"Error en byte de Fin ["+b.getFin()+"]";
	}	
	
	@Test
	public void bloque9728001() throws Exception{
		Block b = Ed2k.getBlockByNum(52, 9728001);
		assert b.getNumBlock()==52L:"Error en numero de bloque";
		assert b.getInicio()==9584640L:"Error en byte de inicio ["+b.getInicio()+"]";
		assert b.getFin()==9727999L:"Error en byte de Fin ["+b.getFin()+"]";
	}
	


	
	
}
