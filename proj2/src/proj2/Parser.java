
import java.io.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.MathContext;

public class Parser {
	
	public String edgesFile;
	public double edges_length;
	public BufferedWriter writer = null;

	public Parser(String edgesFile)  {
		this.edgesFile = edgesFile;
	}

	public void parse() {
		HashMap<String,ArrayList<String>> table = new HashMap<String,ArrayList<String>>();
        try {
            FileReader f_edges = new FileReader(this.edgesFile);
            BufferedReader b_edges = new BufferedReader(f_edges);

			String edges_line = null;

			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("edges_blocks.txt")));

            while((edges_line = b_edges.readLine()) != null) {	

            	this.edges_length++;

				edges_line = edges_line.trim();
				edges_line = edges_line.replaceAll("\\s+",","); 
				String[] arr = edges_line.split(",");
				for(int i=0;i<arr.length;i++) {
					arr[i] = arr[i].replaceAll("[^\\d.]", "");
					System.out.println(arr[i]); 
				}
				ArrayList<String> list = new ArrayList<String>();
				if((list = table.get(arr[0])) == null) {
					ArrayList<String> temp = new ArrayList<String>();
					temp.add(arr[1]);
					table.put(arr[0], temp);
				} else {
					ArrayList<String> temp = new ArrayList<String>();
					temp = table.get(arr[0]);
					temp.add(arr[1]);
					table.put(arr[0], temp);
				}
 			}
			// Loop through table
			Iterator it = table.entrySet().iterator();
    		while (it.hasNext()) {
    			ArrayList<String> list = new ArrayList<String>();
        		Map.Entry pair = (Map.Entry)it.next();
        		list.add(String.valueOf(pair.getKey()));
        		list.add(basePageRank(edges_length));
        		// List<Object> temp = Arrays.asList(pair.getValue());
        		// // List<String> ok = 	Arrays.asList(temp.get(0));
        		// ArrayList stringValues = (ArrayList)pair.getValue().get(0);
        		// System.out.println(stringValues);
        		// String[] stringArray = Arrays.copyOf(pair.getValue(), pair.getValue().length, String[].class);
        		// System.out.println(stringArray);
        		list.add(String.valueOf(String.valueOf(pair.getValue()).split(",").length));
        		String str = pair.getValue().toString();
        		String[] str2 = str.split(",");
        		String builder = "";
        		// String str2 = list.replaceAll(",", "_");
        		// System.out.println(list);
        		// list.add(String.valueOf(pair.getValue().replaceAll(",", "_")));
        		for(Object item : str2) {
        			builder+=((String)item).trim().replaceAll("[^0-9]+", "") + " ";
        			System.out.println(builder);
        		}
        		// System.out.println((temp.get(0)).length);
        		// list.add(String.valueOf(temp.get(0).size()));
        		// list.add(String.valueOf(temp.get(0)));
        		it.remove();
        		builder = builder.trim();
        		builder = builder.replaceAll("\\s+", "_");

        		list.add(builder);

        		System.out.println(list);
        		writer.write("\t" + list.toString().replaceAll("\\[", "").replaceAll("\\]",""));
        		writer.newLine();
    		}
			// Write contents to line
 			// System.out.println(table);
        } catch(Exception e) {
                e.printStackTrace();
        }
	}	
	
	public String basePageRank(double length) {
		BigDecimal b = new BigDecimal(1 / length, MathContext.DECIMAL64);
		return b.toString();
	}
	
	public static void main(String[] args) {
		Parser p = new Parser("edges.txt");
		p.parse();
	}
}
