package com.is.pics.model;

import java.util.Iterator;
import java.util.Set;

public class Item {

	private long itemId;
	private MyUser owner;
	//con postrgresql usare un BLOB è quasi impossibile
	//io non ci so riuscito!
	private byte[] byteImage;

	public Item() {
	}

	public Item(byte[] byteImage, MyUser owner) {
		this.byteImage = byteImage;
		this.owner = owner;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long id) {
		this.itemId = id;
	}

	public byte[] getByteImage() {
		return byteImage;
	}

	public void setByteImage(byte[] byteImage) {
		this.byteImage = byteImage;
	}

	public MyUser getOwner() {
		return owner;
	}

	public void setOwner(MyUser owner) {
		this.owner = owner;
	}

	/*public String createImage(byte[] img){
		return DatatypeConverter.printBase64Binary(img);
	}

	public static Item createItem(MultipartFile file) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		FileCopyUtils.copy(file.getInputStream(), output);
		Item item = new Item();
		item.setByteImage(output.toByteArray());
		return item;
	}*/

    public static Item findById(Set<Item> items,long id){
        Iterator<Item> iter = items.iterator();
        Item it = iter.next();
        while(iter.hasNext() && it.getItemId()!=id)
            it = iter.next();
        //System.out.println("it: "+it.getItemId());
        if(it.getItemId()==id)
            return it;
        else
            return null;
    }
}
