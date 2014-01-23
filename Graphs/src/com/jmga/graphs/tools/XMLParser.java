package com.jmga.graphs.tools;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

import org.xmlpull.v1.XmlPullParser;

import android.R.id;
import android.util.Log;
import android.util.Xml;

import com.jmga.graphs.classes.Graph;

public class XMLParser {
	private static final String TAG = "XMLParser";

	private String storage_path;
	private String current_xml;
	
	private int[] cardinals;
	private Hashtable<String, ArrayList<String>> data;
	
	public XMLParser(){
		storage_path = "";
		current_xml = "";
		
		cardinals = new int[2];
		data = new Hashtable<String, ArrayList<String>>();
	}
	
	public XMLParser(String storage){
		storage_path = storage + "/";
		current_xml = "";

		cardinals = new int[2];
		data = new Hashtable<String, ArrayList<String>>();
	}
	
	public XMLParser(String storage, String xml){
		storage_path = storage + "/";
		current_xml = xml;

		cardinals = new int[2];
		data = new Hashtable<String, ArrayList<String>>();
	}
	
	
	public void setStorage(String storage){
		storage_path = storage;
	}
	
	public void setXml(String xml){
		current_xml = xml;
	}
	
	
	public Graph parseGraph(Graph gr) throws Exception{
		FileInputStream fis = null;
		XmlPullParser xml = Xml.newPullParser();
		
		fis = new FileInputStream(storage_path + current_xml);
		xml.setInput(fis, "UTF-8");
		
		int event = xml.next();
		while(event != XmlPullParser.END_DOCUMENT) {
			ArrayList<String> xmldata = new ArrayList<String>();
			if(event == XmlPullParser.START_TAG)
				for(int i = 0; i < xml.getAttributeCount(); i++) 
					if(xml.getName().equals("graph"))
						if(xml.getAttributeName(i).equals("v"))
							cardinals[0] = Integer.parseInt(xml.getAttributeValue(i));
						else if(xml.getAttributeName(i).equals("a"))
							cardinals[1] = Integer.parseInt(xml.getAttributeValue(i));
					else if(xml.getName().equals("vertex"))
						if(xml.getAttributeName(i).equals("id"))
							data.put(xml.getAttributeValue(i), xmldata);
						else
							if(xml.getAttributeValue(i).length() > 0)
								xmldata.add((xml.getAttributeValue(i).indexOf(".") > 0)
											?xml.getAttributeValue(i).substring(0, xml.getAttributeValue(i).indexOf("."))
											:xml.getAttributeValue(i));

			if(event == XmlPullParser.TEXT && xml.getText().trim().length() != 0){}

			if(event == XmlPullParser.END_TAG){}

			event = xml.next();
		}
		fis.close();
		
		ArrayList<String> aux_read = new ArrayList<String>(cardinals[1]);
		String[] keys = (String[]) data.keySet().toArray(new String[0]);  
        Arrays.sort(keys);  
        for(String id : keys) {  
			ArrayList<String> xmlitem = (ArrayList<String>)data.get(id);
			Log.d(TAG, "Vectice(" + id + ") :1: " + xmlitem.get(0) + "");
			Log.d(TAG, "Vectice(" + id + ") :2: " + xmlitem.get(1) + "");
			Log.d(TAG, "Vectice(" + id + ") :3: " + xmlitem.get(2) + "");
			gr.addNode(Integer.parseInt(xmlitem.get(0)), Integer.parseInt(xmlitem.get(1)));
			// El xml se genera teniendo en cuenta el orden de los vertices respecto a sus coordenadas,
			// entonces si el ID de un vertice adyacente, ya fue registrado anteriormente como vertice
			// principal (hash<"id principal", ArrayList>), la arista que pueda generarse con esos IDs
			// ya existira y por tanto no se a�adira
			Log.d(TAG, "Vectice(" + id + ") :4: " + xmlitem.get(2) + "");
			if(!xmlitem.get(3).isEmpty()){
				String[] adjacent = xmlitem.get(3).split(",");
				for(int i=0; i<adjacent.length; i++){
					if(!aux_read.contains(adjacent[i])){
						gr.addLink(id, adjacent[i], 1);
						Log.d(TAG,"Link::" + id + "," + adjacent[i]);
					}
				}
				aux_read.add(id);
			}else{
				Log.d(TAG,id + ", SIN ADJACENTS");
			}
		}
		
		return gr;
	}
	
}