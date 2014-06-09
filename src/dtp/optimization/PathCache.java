package dtp.optimization;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import dtp.graph.Graph;
import dtp.graph.GraphPoint;
import dtp.graph.GraphTrack;

public class PathCache {
	
	static ConcurrentHashMap<String, GraphTrack> cache;
	static Graph graph;
	static DynamicUpdateRunner dur;

	public PathCache(Graph graph){
		cache = new ConcurrentHashMap<String,GraphTrack>();
		this.graph=graph;
		this.dur=new DynamicUpdateRunner(graph,cache);
		
	}
	
	public void addPath(GraphTrack track,GraphPoint startPoint, GraphPoint endPoint){
		String name = startPoint.getId().toString() +":"+ endPoint.getId().toString();
		cache.put(name, track);
		
	}
	
	public GraphTrack getPath(GraphPoint startPoint, GraphPoint endPoint){
		return cache.get(startPoint.getId().toString() +":"+ endPoint.getId().toString());
		
		
	}
	
	public boolean isCached(GraphPoint startPoint, GraphPoint endPoint){
	//	System.out.println(startPoint.getId().toString() + endPoint.getId().toString());
		return cache.containsKey(startPoint.getId().toString() +":"+ endPoint.getId().toString());
	}
	
	public  void flush(){
		if(dur.b.compareAndSet(false, true)==false)return;
	//	dur.busy=true;
		Thread t = new Thread(dur);
		t.start();
		//cache.clear();
		
	}
	
	public boolean isBusy(){
		//return dur.busy;
		return dur.b.get();
	}
	
}
