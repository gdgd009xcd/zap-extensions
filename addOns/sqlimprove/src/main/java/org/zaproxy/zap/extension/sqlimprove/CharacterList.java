package org.zaproxy.zap.extension.sqlimprove;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

public class CharacterList extends AbstractList <Character>{
	private  String string;

    CharacterList (String string)
    {
        this.string = string;
    }

    @Override
    public Character get (int index)
    {
        return Character.valueOf (string.charAt (index));
    }

    @Override
    public int size ()
    {
        return string.length ();
    }
    
    @Override
    public boolean add(Character c){
    	this.string += c.toString();
    	return true;
    }
    
    @Override
    public void add(int index, Character c){
    	String newvalue = c.toString() + this.string;
    	this.string = newvalue;
    }
    
    
    public String getString(){
    	return this.string;
    }
}