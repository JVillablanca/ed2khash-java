package com.dwarfanvil.utils.ed2khash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base32;

/**
 * @author Juan Villablanca
 * 
 */
public class Aich {

	private static final Integer FULL_BLOCK_SIZE=184320;
	private static final Integer ED2K_CHUNK_SIZE=9728000;
	private MessageDigest md;
	private Base32 base32;
	private byte[][] hashchunks= new byte[53][];
	private int numBlocks;
	private int maxBlock=53;
	private String hashAICH;

	private Map<Integer,byte[][]> hashTable;
	
	public enum TipoHash {
		AICH_HASH_LEFT_BRANCH,
		AICH_HASH_RIGHT_BRANCH,
		AICH_HASH_FULL_TREE}



	public Aich() throws NoSuchAlgorithmException {
		md=MessageDigest.getInstance("SHA-1");
		base32 = new Base32();
		numBlocks=0;
		hashTable= new HashMap<Integer,byte[][]>();
	}

	public byte[][] getHashChunksSet(){
		byte[][] arrayChunks= new byte[numBlocks][];
		for(int i=0;i<numBlocks;i++) arrayChunks[i]= hashchunks[i];
		return arrayChunks;
	}


	public void update(byte[] chunk,int largo) throws Ed2kException{

		if(largo<=0){
			throw new Ed2kException("Se esperaba un largo mayor a 0");
		}
		if(largo>ED2K_CHUNK_SIZE){
			throw new Ed2kException("Se esperaba un largo menor o igual al Chunk");
		}

		int index=0;
		do{
			md.reset();	
			if((largo-index)>=FULL_BLOCK_SIZE){
				md.update(chunk, index, FULL_BLOCK_SIZE);
				index += FULL_BLOCK_SIZE;
			}else{
				md.update(chunk, index, largo-index);
				index=largo;
			}
			if(numBlocks>=maxBlock){
				maxBlock+=53;
				byte[][] newarray=new byte[maxBlock][];
				for(int i=0;i<numBlocks;i++) newarray[i]=hashchunks[i];
				hashchunks=newarray;
			}
			hashchunks[numBlocks++]=md.digest();
		}while(index<largo);

	}

	public void digest(){

		if(numBlocks<=53){
			byte[] hashBloque=hashTree(hashchunks,numBlocks,TipoHash.AICH_HASH_LEFT_BRANCH,0);
		    hashAICH=base32.encodeAsString(hashBloque);
		}else if(numBlocks>53&&numBlocks<=106){
			
			byte[][] myhash = new byte[53][];
			for(int i=0;i<53;i++) myhash[i]=hashchunks[i];
			byte[] hashLeft=hashTree(myhash,53,TipoHash.AICH_HASH_LEFT_BRANCH,0);
			myhash = new byte[53][];
			for(int i=0;i<(numBlocks-53);i++) myhash[i]=hashchunks[i+53];
			byte[] hashRight=hashTree(myhash,numBlocks-53,TipoHash.AICH_HASH_RIGHT_BRANCH,0);
			byte[][] pair0 = new byte[2][];
			pair0[1]=hashLeft;
			
			byte[][] pair1 = new byte[2][];
			pair1[0]=hashRight;
			
			hashTable.put(0, pair0);
			hashTable.put(1, pair1);
			byte[] hashBloque=hashTree(hashchunks,numBlocks,TipoHash.AICH_HASH_FULL_TREE,2);
		    hashAICH=base32.encodeAsString(hashBloque);
			
		}else{
			
			byte[][] myhash = new byte[53][];
			for(int i=0;i<53;i++) myhash[i]=hashchunks[i];
			byte[] hash1=hashTree(myhash,53,TipoHash.AICH_HASH_LEFT_BRANCH,0);
			byte[][] pair0 = new byte[2][];
			pair0[1]=hash1;
			hashTable.put(0, pair0);
			
			int index=53;
			byte[][] pair;
			int numchunks=1;
			while(index+53<numBlocks){
				myhash = new byte[53][];
				for(int i=0;i<53;i++) myhash[i]=hashchunks[index+i];
				byte[] hashLeft =hashTree(myhash,53,TipoHash.AICH_HASH_LEFT_BRANCH,0);	
				byte[] hashRight=hashTree(myhash,53,TipoHash.AICH_HASH_RIGHT_BRANCH,0);
				pair = new byte[2][];
				pair[1]=hashLeft;
				pair[0]=hashRight;
				hashTable.put(numchunks++, pair);
				index+=53;
			}
			myhash = new byte[53][];
			for(int i=0;i<(numBlocks-index);i++) myhash[i]=hashchunks[index+i];
			byte[] hashN=hashTree(myhash,numBlocks-index,TipoHash.AICH_HASH_RIGHT_BRANCH,0);
			byte[][] pairN = new byte[2][];
			pairN[0]=hashN;
			hashTable.put(numchunks, pairN);
			byte[] hashBloque=hashTree(hashchunks,numBlocks,TipoHash.AICH_HASH_FULL_TREE,numchunks+1);
		    hashAICH=base32.encodeAsString(hashBloque);
		}
	}
	
	

	private byte[] hashTree(byte[][] hashes,int largo,TipoHash tipo,int numChunks){
		int index=0;
		long blocks=0;
		int level=0;
		int is_left_branch=(tipo==TipoHash.AICH_HASH_RIGHT_BRANCH)?0:1;
		long path = is_left_branch;
		long[] blocks_stack; 
		byte[][]sha1_stack;
		blocks_stack = new long[56];
		sha1_stack   = new byte[56][];
		if(tipo==TipoHash.AICH_HASH_FULL_TREE){
			blocks=numChunks;
		}else{
			blocks=largo;
		}
		blocks_stack[0]=blocks;
		while(1==1){
			byte[] sha1_message, leaf_hash;
			while(blocks>1){
				blocks = (blocks + (path & 0x1) ) / 2;
				level++;
				blocks_stack[level] = blocks;
				path = (path << 1) | 0x1;
			}
			leaf_hash=hashes[index];
			if(tipo==TipoHash.AICH_HASH_FULL_TREE){
				is_left_branch=(int)path &0x1;
				leaf_hash = hashTable.get(index)[is_left_branch];
			}
			index++;
			for (; level > 0 && (path & 0x01) == 0; path >>= 1) {
				md.reset();	
				md.update(sha1_stack[level], 0, 20);
				md.update(leaf_hash, 0, 20);
				leaf_hash=md.digest();
				level--;
			}
			sha1_stack[level]=leaf_hash;
			if (level == 0) break;
			path &= ~0x1;
			is_left_branch = (int)(path >> 1) & 1;
			blocks_stack[level] = (blocks_stack[level - 1] + 1 - is_left_branch) / 2;
			blocks = blocks_stack[level];
		}
		return sha1_stack[0];
	}
	public String getHashAICH() {
		return hashAICH;
	}
}
