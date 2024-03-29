import java.util.ArrayList;
import java.util.Arrays;

import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;

public final class Alg {

	public static void buildILP(IloCplex cplex, Network nw, IloIntVar[] y) {
		try {
			IloLinearNumExpr lin;
			Integer[] edge;
			nw.x = cplex.boolVarArray(nw.N);
			nw.y = cplex.boolVarArray(nw.N);
			nw.e = cplex.boolVarArray(nw.E);

			for (int i=0; i<nw.N; i++) {
				lin = cplex.linearNumExpr();
				for (int j=0; j<nw.E; j++) {
					edge = nw.edgeList.get(j);
					if (edge[0] == i) {
						lin.addTerm(1.0, nw.e[j]);
					}
				}
				cplex.addEq(nw.x[i], lin);
			}

			for (int i=0; i<nw.N; i++) {
				lin = cplex.linearNumExpr();
				for (int j=0; j<nw.E; j++) {
					edge = nw.edgeList.get(j);
					if (edge[1] == i) {
						lin.addTerm(1.0, nw.e[j]);
					}
				}
				cplex.addEq(nw.y[i], lin);
				cplex.addGe(nw.y[i], y[i]);
				cplex.addGe(y[i], 0);
			}
			double[] coefs = new double[nw.N];
//			double[] coefs = new double[nets.get(0).N * nets.size()];
			Arrays.fill(coefs, 1.0);
			cplex.addMaximize(cplex.scalProd(coefs, y));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<Integer> getMM(IloCplex cplex, IloIntVar[] y, TimeUtil t) {
		ArrayList<Integer> mmList = new ArrayList<>();
		try {
			t.start();
			if (cplex.solve()) {
				t.end();
				for (int i=0;i<y.length; i++) {
					if (cplex.getValue(y[i])==1) {
						mmList.add(i);
					}
				}
			} else {
				System.out.println("network error: MM size = 0");
				System.exit(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mmList;
	}
}