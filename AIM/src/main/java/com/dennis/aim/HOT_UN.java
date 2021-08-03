package com.dennis.aim;

public class HOT_UN {
	public static int stockOwned=500;
	public static float cash=5000f;
	
	public static void main(String[] args) {
		
		
		// BMO bad
		//float[] stockPriceBMO = {83.199997f,87f,85.830002f,85.330002f,88.809998f,96.900002f,98.559998f,102.110001f,99.410004f,97.120003f,91.290001f,95.790001f,94.889999f,89.699997f,94.940002f,98.82f,99.330002f,100.589996f,100.910004f,97.480003f,97.290001f,97.370003f,100.099998f,102f,103.199997f,107.580002f,107.639999f,98.699997f,99.989998f,88.580002f,95.879997f,103.07f,100.510002f,105.839996f,98.120003f,98.43f,98.639999f,90.989998f,98.139999f,97.849998f,102.300003f,101.040001f,101.209999f,90.830002f,67.510002f,69.620003f,68.300003f,72.599998f,73.5f,82.25f,78.18f,79.699997f,96.199997f,97.010002f,95.910004f,105.330002f,112f,116.199997f,127.050003f,128.220001f,124.75f};
		
		//TD bad
		float[] stockPrice3 ={43.509998f,44.490002f,44.439999f,45.599998f,47.869999f,49.57f,51.810001f,52.110001f,50.150002f,47.23f,47.900002f,50.630001f,51.720001f,54.110001f,56.299999f,57.02f,57.450001f,58.82f,60.73f,58.439999f,56.849998f,56.130001f,58.490002f,57.52f,59.32f,59.619999f,61.650002f,55.93f,56.32f,49.330002f,56.52f,57.48f,54.75f,56.990002f,54.650002f,58.84f,58.290001f,53.82f,57.810001f,57.220001f,57.700001f,56.259998f,55.220001f,51.48f,40.439999f,40.790001f,43.110001f,44.799999f,44.5f,49.639999f,46.490002f,44.82f,54.450001f,56.77f,56.849998f,61.439999f,65.190002f,69.080002f,72.699997f,70.410004f,67.110001f};
		
		//TZR.V - good
		float[] stockPrice2 = {0.045f,0.035f,0.03f,0.025f,0.025f,0.06f,0.035f,0.045f,0.04f,0.03f,0.03f,0.025f,0.02f,0.02f,0.02f,0.015f,0.015f,0.01f,0.025f,0.02f,0.015f,0.02f,0.02f,0.015f,0.015f,0.025f,0.02f,0.015f,0.01f,0.005f,0.005f,0.015f,0.015f,0.015f,0.015f,0.02f,0.01f,0.01f,0.015f,0.01f,0.005f,0.005f,0.005f,0.005f,0.005f,0.005f,0.005f,0.005f,0.005f,0.005f,0.01f,0.01f,0.005f,0.01f,0.01f,0.02f,0.015f,0.01f,0.01f,0.005f,0.005f};
		
		
		float[] stockPrice4 = {0.91f,0.89f,0.98f,0.97f,0.63f,0.71f,0.92f,0.96f,0.85f,0.64f,0.57f,0.59f,0.66f,0.65f,0.58f,0.53f,0.44f,0.4f,0.35f,0.35f,0.33f,0.28f,0.21f,0.24f,0.2f,0.21f,0.23f,0.24f,0.32f,0.4f,0.34f,0.3f,0.3f,0.24f,0.24f,0.24f,0.28f,0.5f,0.49f,0.5f,0.43f,0.51f,0.5f,0.45f,0.4f,0.81f,0.78f,0.82f,0.79f,1.03f,1.02f,1.18f,1.02f,0.97f,1.05f,0.74f,0.81f,0.82f,0.74f,0.65f,0.465f};
		
		float[] stockPrice=stockPrice3;
		float interest=-10;
		
		stockOwned=Math.round(cash/stockPrice[0]);

			Linef lineInt = new Linef();

			lineInt.stockPrice = stockPrice[0];
			lineInt.stockValue = 5000;
			lineInt.safe = 500;
			lineInt.cash = cash;
			lineInt.stockOwned = stockOwned;
			lineInt.portfolioControl = 5000;
			lineInt.portfolioValue = 10000;
			
			
			
			
			
			
			Line.printHeader();
			System.out.println();
			lineInt.printValues();
			System.out.println();
			
			
			Linef prevLine=lineInt;
			for(int i=0;i<stockPrice.length;i++) {
				Linef l= new Linef(stockPrice[i], prevLine.sharesBoughtSold, prevLine.portfolioControl, prevLine.marketOrder,prevLine.action,
						prevLine.interest,interest);
				l.printValues();
				System.out.println();
				prevLine=l;
			}

		}


}
