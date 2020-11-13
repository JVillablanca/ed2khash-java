package com.dwarfanvil.utils.ed2khash;

/**
 * @author Juan Villablanca
 *
 */
public class Block {

	private long numBlock;
	private long inicio;
	private long fin;
	
	public Block(long numBlock, long inicio, long fin) {
		super();
		this.numBlock = numBlock;
		this.inicio = inicio;
		this.fin = fin;
	}
	
	public long getNumBlock() {
		return numBlock;
	}
	public long getInicio() {
		return inicio;
	}
	public long getFin() {
		return fin;
	}
	

}
