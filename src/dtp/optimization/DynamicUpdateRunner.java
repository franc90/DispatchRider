package dtp.optimization;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import dtp.graph.Graph;
import dtp.graph.GraphTrack;

public class DynamicUpdateRunner implements  Runnable{
	static Graph graph;
	static ConcurrentHashMap<String, GraphTrack> cache;
	static boolean busy;
	static AtomicBoolean b;
	static DijkstraUpdater dij;
	
	public DynamicUpdateRunner(Graph graph,ConcurrentHashMap<String, GraphTrack> cache){
		this.graph=graph;
		this.cache=cache;	
		busy =false;
		b=new AtomicBoolean();
		this.dij=new DijkstraUpdater(graph);
	}
	
	



	public   void run(){
	//	ConcurrentHashMap<String, GraphTrack> cacheTmp=new ConcurrentHashMap<String, GraphTrack>();

		for (String path : cache.keySet()) 
		{
			
			System.out.println("Update: "+ path);
			try
			{					
					cache.replace(path,dij.findTrack(cache.get(path).getFirst(), cache.get(path).getLast()));
				
			}catch(Exception e)
			{
				
				cache.remove(path);
				
				//e.printStackTrace();
				/*
				try
				{
				System.out.println(path);
				}catch(Exception e2){
					
				}
				try{
				System.out.println(cache.get(path).getFirst().getName());
				}catch(Exception e2){
					
				}
				try{
				System.out.println(cache.get(path).getLast().getName());
				}catch(Exception e2){
					
				}
					*/
				//already updated
			}
			
		}
		
		b.set(false);
	}
	
	
}
