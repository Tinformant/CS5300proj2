package proj2;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class singleNodeMaster {
//	will handle the reading of the information file
//	in addition, will facilitate the pagerank algorithm
//	through the singleMapper and single Reducer classes
	
//	piggy backs the residual calculations by doing an extra iteration
	
	public static enum residualCounters{
//		static makes the counter locally accessible
//		enum makes it countable
		residualCounter
	}
	
//	declaration of variables
	public static final int totalNodes = 685230;
	private static final int numIterations = 6;
	public static final float dampingFactor = (float) 0.85;
	public static final int longator = 100000;
	
	public static void main(String[] args) throws Exception{

		if (args.length != 2){
//			need the correct inputs
			throw new Exception("wrong input/output files");
		}
		
//		FILE READING
//		when the master node is run, the two parameters provided should be
//		the input and output file
		String inputPath = args[0];
		String outPath = args[1];
		
		
		
		for (int i = 0; i < numIterations; i++){
//			create job instance
			Job job = Job.getInstance();
//			define this class as the master node
			job.setJarByClass(proj2.singleNodeMaster.class);
			
			job.setJobName("pageRank-" + (i + 1));
			
			job.setMapperClass(proj2.singleMapper.class);
			job.setReducerClass(proj2.singleReducer.class);
			
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			
//			For the initial iteration, we will be reading from the preprocessed File
			
			if (i == 0){
				FileInputFormat.addInputPath(job, new Path(inputPath));
//			Subsequent Files will be read through an intermediary memory bank
			} else {
				FileInputFormat.addInputPath(job, new Path(outPath + "/iteration" + i));
			}
			
			FileOutputFormat.setOutputPath(job, new Path(outPath + "/iteration" + (i+1)));
			
			job.waitForCompletion(true);
			
			float avgResidual = job.getCounters().findCounter(residualCounters.residualCounter).getValue();
			avgResidual = avgResidual / longator / totalNodes;
			
			String residualError = String.format("%.4f", avgResidual);
			System.out.println("Average Residual at iteration " + i + "is:" +residualError);
			
			
//			reset the counter for next round
			job.getCounters().findCounter(residualCounters.residualCounter).setValue(0L);
		
		}
//		load the related text file
//		open(edgelist.txt);
		
//		map the pagerank
//		reduce it?
//		print out results in a specified location
	}

}
