package com.wse.model;

public class VolumeIndexedObject {
	private int volumeId;
	private StringBuilder volumeIndexContent;
	
	public VolumeIndexedObject(int volumeId, StringBuilder volumeIndexContent) {
		super();
		this.volumeId = volumeId;
		this.volumeIndexContent = volumeIndexContent;
	}
	public int getVolumeId() {
		return volumeId;
	}
	public void setVolumeId(int volumeId) {
		this.volumeId = volumeId;
	}
	public StringBuilder getVolumeIndexContent() {
		return volumeIndexContent;
	}
	public void setVolumeIndexContent(StringBuilder volumeIndexContent) {
		this.volumeIndexContent = volumeIndexContent;
	}
}
