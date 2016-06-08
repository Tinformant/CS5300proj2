package proj2;

import java.io.IOException;


import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;


public class singleMapper extends Mapper<LongWritable, Text, Text, Text>{
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		
//		key is the node reference
//		value contains ALL the information provided by the initial read file
		
//		values should be in format "node#, pageRank, degrees, outgoingList"
//		delimiter is ","

		String line = value.toString();
//		clear whitespace
		line = line.trim();
		
//		holder = [node#, pageRank, degrees, outgoingList]
		String[] holder = line.split(",");
		
//		All nodes should have an edgelist, otherwise we have a sink
//		
		String edgeList = holder[3];

		Float pageRank = new Float(holder[1]);
		Integer degree = new Integer(holder[2]);
		
//		the key is the node#
		Text mapKey = new Text(holder[0]);
//		we need to keep the member of the edgelist
//		the pagerank itself is also important lol
		Text mapValue = new Text("pR " + holder[1] + " " + String.valueOf(degree) + " " + edgeList );
		
//		Write to corresponding node its properties
		context.write(mapKey, mapValue);
		
		
		
		Float rankPortion = new Float(pageRank/degree);
		mapValue = new Text("pC "+ String.valueOf(rankPortion));
		
//		The edgelist is assumed to come in the format: 1_2_3 ...
		String[] edgeListCollection = edgeList.split("_");
		
		for (int i = 0; i < edgeListCollection.length; i++){
			mapKey = new Text(edgeListCollection[i]);
//			Write to other nodes the portion of contribution from this node
			context.write(mapKey, mapValue);
		}
		
	}
}
