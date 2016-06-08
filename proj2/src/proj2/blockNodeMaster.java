package proj2;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import proj2.singleNodeMaster.residualCounters;


public class blockNodeMaster {

	public static enum residualCounters{
		residualCounter
	};
	public static final int totalNodes = 685230;
	public static final float dampingFactor = (float) 0.85;
	public static final int longator = 10000;
	public static final int totalBlocks = 68;
	public static final float threshold = 0.001f;
	public static final int[] blockBoundaries = { 0, 10328, 20373, 30629, 40645,
		50462, 60841, 70591, 80118, 90497, 100501, 110567, 120945,
		130999, 140574, 150953, 161332, 171154, 181514, 191625, 202004,
		212383, 222762, 232593, 242878, 252938, 263149, 273210, 283473,
		293255, 303043, 313370, 323522, 333883, 343663, 353645, 363929,
		374236, 384554, 394929, 404712, 414617, 424747, 434707, 444489,
		454285, 464398, 474196, 484050, 493968, 503752, 514131, 524510,
		534709, 545088, 555467, 565846, 576225, 586604, 596585, 606367,
		616148, 626448, 636240, 646022, 655804, 665666, 675448, 685230 };
	
	
	public static void main(String[] args) throws Exception {
		
		if (args.length != 2){
//			need the correct inputs
			throw new Exception("wrong input/output files");
		}
		
		String inputPath = args[0];
		String outPath = args[1];
		
		int iterationCounter = 0;
		float avgResidual = 0.0f;
		
		do {
//			create job instance
			Job job = Job.getInstance();
//			define this class as the master node
			job.setJarByClass(proj2.blockNodeMaster.class);
			
			job.setJobName("pBlock_" + (iterationCounter + 1));
			
			job.setMapperClass(proj2.blockMapper.class);
			job.setReducerClass(proj2.blockReducer.class);
			
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			
			if (iterationCounter == 0) {
                FileInputFormat.addInputPath(job, new Path(inputPath)); 	
            // otherwise use the output of the last pass as our input
            } else {
            	FileInputFormat.addInputPath(job, new Path(outPath + "/tmp"+iterationCounter)); 
            }
            // set the output file path
            FileOutputFormat.setOutputPath(job, new Path(outPath + "/tmp"+(iterationCounter+1)));
            
            // execute the job and wait for completion before starting the next pass
            job.waitForCompletion(true);
			
            avgResidual = job.getCounters().findCounter(residualCounters.residualCounter).getValue();
			avgResidual = avgResidual / longator / totalNodes / totalBlocks;
			
			String residualError = String.format("%.4f", avgResidual);
			System.out.println("Average Residual at iteration " + iterationCounter + "is:" +residualError);
			
			
//			reset the counter for next round
			job.getCounters().findCounter(residualCounters.residualCounter).setValue(0L);
			
			iterationCounter++;
            
		} while (avgResidual > threshold);

		
	}

}
