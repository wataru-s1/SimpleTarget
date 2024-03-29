import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ilog.concert.IloIntVar;

public final class Network {

	/*
	 * basic variables
	 */
	public String name;
	public int N;
	public int E;
	public double[][] adjMat;
	public ArrayList<String> targetList;
	public ArrayList<String> nodeList;
	public ArrayList<Integer[]> edgeList;
	public ArrayList<Integer> cmds;
	public ArrayList<Integer> rmds;
	public ArrayList<Integer> imds;
	public IloIntVar[] x;   //initialized in MDSAlg.buildILP()
	public IloIntVar[] y;   //initialized in MDSAlg.buildILP()
	public IloIntVar[] e;   //initialized in MDSAlg.buildILP()

	public Network(Path file, Path module) {
		cmds = new ArrayList<>();
		rmds = new ArrayList<>();
		imds = new ArrayList<>();
		edgeList = new ArrayList<>();
		targetList = new ArrayList<>();
		readEdgeList(file,module);
	}

	private void readEdgeList(Path file, Path module) {
		try {
			List<String> lines = Files.readAllLines(file,StandardCharsets.UTF_8);
			String BR = lines.get(0).replaceAll("[A-Za-z0-9\\.\\-]","");
			ArrayList<String[]> edgePairList = new ArrayList<>();
			HashSet<String> nodeSet = new HashSet<>();

			String[] edgePair;
			
			for (String line : lines) {
				edgePair = line.split(BR);
				if (edgePair.length == 2) {
					edgePairList.add(edgePair);
					nodeSet.add(edgePair[0]);
					nodeSet.add(edgePair[1]);
				} else {
					System.out.println(line+" is not separatable to 2 elements");
				}
			}
			
			lines = null;

			nodeList = new ArrayList<>(nodeSet);
			
			nodeSet = null;

			E = edgePairList.size();
			N = nodeList.size();
			adjMat = new double[N][N];
			
			readModule(module);

			for (int i=0; i<E; i++) {
				int j = nodeList.indexOf(edgePairList.get(i)[0]);
				int k = nodeList.indexOf(edgePairList.get(i)[1]);
				adjMat[j][k] = 1;
				Integer[] edge = {j, k};
				edgeList.add(edge);
			}

			edgePairList = null;

			E = edgeList.size();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void readModule(Path module) {
		try {
			List<String> mod= Files.readAllLines(module,StandardCharsets.UTF_8);
			for (String node : mod) {
				targetList.add(node);
			}
			for (String node : nodeList) {
				if (!mod.contains(node)) {
					int i = nodeList.indexOf(node);
					adjMat[i][i] = 1;
					Integer[] edge = {i, i};
					edgeList.add(edge);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setNull() {
		adjMat = null;
		targetList = null;
		nodeList = null;
		edgeList = null;
		cmds = null;
		rmds = null;
		imds = null;
		x = null;
		y = null;
		e = null;
	}
}
