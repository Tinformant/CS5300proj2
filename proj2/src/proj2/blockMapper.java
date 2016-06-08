package proj2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.Mapper.Context;


public class blockMapper extends Mapper<LongWritable, Text, Text, Text> {
	
	public static int[] blockBounds = blockNodeMaster.blockBoundaries;
	public static int alligator = blockNodeMaster.longator;
	
	
//	takes in a string input for key reference
//	outputs which block the unit belongs to
	public static int getBlockIndex(String nodeKey){
		Integer nKey = Integer.parseInt(nodeKey);
		double supposedIndex = Math.floor(1.0 * nKey / alligator);
		int maybeNeighbor = blockBounds[(int)supposedIndex];
		if (nKey < maybeNeighbor){
			return (int)supposedIndex - 1;
		} else {
			return (int)supposedIndex;
		}
	}
	
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
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
		Text nodeKey = new Text(holder[0]);
		
		Text blockKey = new Text(String.valueOf(getBlockIndex(holder[0])));
		
//		Write to block the identity value of each node
		Text identityValue = new Text("B_ID " + " " + nodeKey + " "+ String.valueOf(pageRank) + " " + String.valueOf(degree) + " " + edgeList);
		context.write(blockKey, identityValue);
		
		Float rankPortion = new Float(pageRank/degree);
		
		
		Map<String,String> blockHash = new HashMap<String,String>();
		String[] edgeListCollection = edgeList.split("_");
		for (int i = 0; i< edgeListCollection.length; i++){
			String tempNodeID = edgeListCollection[i];
			String tempBlkKey = String.valueOf(getBlockIndex(tempNodeID));
			
			if (blockHash.containsKey(tempBlkKey)){
				 blockHash.put(tempBlkKey, String.valueOf(tempNodeID));
			}else {
				String prepend = blockHash.get(tempBlkKey);
				blockHash.put(tempBlkKey, prepend + "_" + String.valueOf(tempNodeID));
			}
		}
		
		String portionMsg = "B_Por "+ " " +  String.valueOf(rankPortion);
		
		for (Entry<String, String> entry : blockHash.entrySet()) {

			Text theblkKey = new Text(String.valueOf(entry.getKey())); //block id
			portionMsg += (" " + entry.getValue()); // "B_Por rankPortion nodeList
			Text pRportions = new Text(portionMsg);
			context.write(theblkKey, pRportions);
		}
		
		
	}
	
	
}
