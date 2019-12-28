package org.zaproxy.zap.extension.sqlimprove;

import java.util.List;

import org.apache.log4j.Logger;

public class LcsOnp<T> extends AbstractLcsComparator<T>{
	
    int M;
    int N;
	OnpV onpV[] = null;
	List<T> listX = null;
	List<T> listY = null;
	int delta=0;
	int offset=0;
	int distance = 0;
	int delta_offset = 0;
	int P;
	
	LcsOnp(Logger _log){
		super(_log);
		init();
		
	}
	
	void init() {
		onpV = null;
		listX = null;
		listY = null;
		delta=0;
		offset=0;
		distance = 0;
		delta_offset = 0;
		P = 0;
	}
	
	@Override
	public int calcLCS(List<T> a, List<T> b, LcsBuilder<T> result) {//return lcs size
		long startTime = System.currentTimeMillis();
		int D = onpCalc(a, b, result);
		long onpcalctime= System.currentTimeMillis() - startTime;
		//System.out.println("ONP D:" + D + " time:" + onpcalctime);
		if(result!=null) {
			result.clear();
		}
		
		return getLCSinternal(result);
	}
	
	
	protected int onpCalc(List<T> A, List<T> B, LcsBuilder<T> result) {//return D = delta + 2p
		init();
		if(result!=null) {
			result.setABreverse(false);
		}
		
		M = A.size();
		N = B.size();

		if(M > N) {
			if(result!=null) {
				result.setABreverse(true);
			}
			return onpCalc(B, A, null);
		}
		listX = A;
		listY = B;
		onpV = new OnpV[M+N+1];
		delta = N-M;
		offset = M ;
		delta_offset = delta + offset;
		OnpV prev = null;
		for(int p=0; p<=M; p++) {
			int k;
			int oper = 0;
			
			int kstart = offset - p;
			for (k = kstart; k < delta_offset; ++k) {
				
				int x = 0;
				//prev = null;
				//int x = (p == 0) ? (k == 0 ? 0 : onpV[k - 1 + offset].x)
				//		: (k == -p ? onpV[k + 1 + offset].x + 1 : Math.max(onpV[k + 1 + offset].x + 1, onpV[k - 1 + offset].x));
				if(p==0) {
					if(k==offset) {//k==0:k==offset
						//0
						oper = 0;
						//prev = null;
						x = 0;
					}else {
						
						oper = -1;
						//onpV[k - 1 + offset].x
						prev = onpV[k-1];
						x = prev.x;
						prev.setRefered();
					}
				}else if(k==kstart) {//k==-p: k == kstart
					//onpV[k + 1 + offset].x + 1 
					prev = onpV[k+1];
					x = prev.x + 1;
					prev.setRefered();
					//x = prev.x + 1;
					oper = 1;
				}else {
					//Math.max(onpV[k + 1 + offset].x + 1, onpV[k - 1 + offset].x)
					OnpV onpVxplus =  onpV[k+1];
					OnpV onpVxminus = onpV[k-1];
					int xplus = onpVxplus.x+1;
					int xminus = onpVxminus.x;
					if(xplus > xminus) {
						x = xplus;
						//prev = onpVxplus;
						oper = 1;
						onpVxplus.setRefered();
					}else {
						x = xminus;
						//prev = onpVxminus;
						oper = -1;
						onpVxminus.setRefered();
					}
				}
				
				
				if(onpV[k]==null) {
					onpV[k] = new OnpV(x, p, oper);
				}else {
					onpV[k].setOper(x, p, oper);
				}
				snake(k, x, A, B, onpV[k]);
				
				
			}
			kstart = p+delta_offset;
			for (k = kstart; k > delta_offset; k--) {
				//startTime = System.currentTimeMillis();
				int x = 0;
				//int x = (k == (delta + p)) ? onpV[k - 1 + offset].x
				//		: Math.max(onpV[k + 1 + offset].x + 1, onpV[k - 1 + offset].x);
				if(k==kstart) {
					//onpV[k - 1 + offset].x
					prev = onpV[k - 1];
					x = prev.x;
					prev.setRefered();
					oper = -1;
				}else {
					OnpV onpVxplus =  onpV[k+1];
					OnpV onpVxminus = onpV[k-1];
					int xplus = onpVxplus.x+1;
					int xminus = onpVxminus.x;
					if(xplus > xminus) {
						x = xplus;
						//prev = onpVxplus;
						onpVxplus.setRefered();
						oper = 1;
					}else {
						x = xminus;
						//prev = onpVxminus;
						onpVxminus.setRefered();
						oper = -1;
					}
				}
				
				if(onpV[k]==null) {
					onpV[k] = new OnpV(x, p, oper);
				}else {
					onpV[k].setOper(x, p, oper);
				}
				snake(k, x, A, B, onpV[k]);
				
			}
			k = delta_offset;
			int x = 0;
			//int x = (p == 0) ? (k == 0 ? 0 : onpV[k - 1 + offset].x)
			//		: Math.max(onpV[k + 1 + offset].x + 1, onpV[k - 1 + offset].x);
			if(p==0) {
				if(k==offset) {//k==0:k==offset
					//0
					x = 0;
					//prev = null;
					oper = 0;
				}else {
					//onpV[k - 1 + offset].x
					prev = onpV[k - 1];
					x = prev.x;
					prev.setRefered();
					oper = -1;
				}
			}else {
				OnpV onpVxplus =  onpV[k+1];
				OnpV onpVxminus = onpV[k-1];
				int xplus = onpVxplus.x+1;
				int xminus = onpVxminus.x;
				if(xplus > xminus) {
					x = xplus;
					//prev = onpVxplus;
					onpVxplus.setRefered();
					oper = 1;
				}else {
					x = xminus;
					//prev = onpVxminus;
					onpVxminus.setRefered();
					oper = -1;
				}
			}
			
			if(onpV[k]==null) {
				
				onpV[k] = new OnpV(x, p, oper);
				
			}else {
				
				onpV[k].setOper(x, p, oper);
				
			}
			snake(k, x, A, B, onpV[k]);
			
						
			if (onpV[delta_offset].x == M) {
				// return delta + 2 * p;
				distance = delta + 2*p;
				if(log!=null)
					log.debug("D="+distance + " delta: " + delta + " offset:" + offset + " p=" +p);
				
				P = p;
				return distance;
			}
        }
		
		return -1;
		
	}
	
	
	
	int snake(int k, int x, List<T> A, List<T> B, OnpV V) {
		int y = x + k - offset;// k = y-x , x = y - k
		// A.len <= M B.len <=N
		//System.out.println("snake start: x, y="  +x + ","+ y);
		int stx = x;
		while(x < M && y < N && A.get(x).equals( B.get(y))) {//T object must have equals method. primitive does'nt have it. so In this Class, T primitive cannot support.
			x++;y++;
		}
		if(x>stx) {
			V.setSnake(x - stx);
		}
		//System.out.println("snake end: x, y="  +x + ","+ y);
		return x;
	}
	
	
	
	public List<T> getLCS(LcsBuilder<T> lcsBuilder) {//
		getLCSinternal(lcsBuilder);
		return lcsBuilder.getLCS();
	}
	
	protected int getLCSinternal(LcsBuilder<T> lcsBuilder) {//
		int xi = M;
		int yi = N;
		int lcscnt=0;
		if(lcsBuilder!=null) {
			lcsBuilder.setReverseLCS();
		}
		//long startTime = System.currentTimeMillis();
		if(onpV!=null) {
			OnpV cv = onpV[delta_offset];
			int k = delta_offset;
			int p = P;
			int oper = cv.oper;
			int snakecnt = cv.snakecnt;
			//System.out.println("x,y=" + (xi) + "," + (yi) + " oper:" + oper + " V" + p + "(" + (k - offset) + ") snake=" + snakecnt);
			
			for(int scnt = snakecnt; scnt>0; scnt--) {
				
				/*{
					int oldx = xi;
					int oldy = yi;
					System.out.println(oldx + "," + oldy + "->" + (xi-1) + "," + (yi-1));
				}*/
				
				xi--;yi--;
				if(xi>=0&&xi<M) {
					if(lcsBuilder!=null) {
						//lcsBuilder.add(0, listX.get(xsn));//disasterous performance problem O(n)
						lcsBuilder.append(listX.get(xi));// value stored reverse order
					}
					lcscnt++;
				}
			}
			if(oper==1) {
				/*{
					int oldx = xi;
					int oldy = yi;
					System.out.println(oldx + "," + oldy + "->" + (xi-1) + "," + yi);
				}*/
				xi--;
				if(xi>=0&&xi<M&&lcsBuilder!=null) {
					lcsBuilder.appenddiffA(listX.get(xi));
				}
			}else if(oper==-1) {
				/*{
					int oldx = xi;
					int oldy = yi;
					System.out.println(oldx + "," + oldy + "->" + xi + "," + (yi-1));
				}*/
				yi--;
				if(yi>=0&&yi<N&&lcsBuilder!=null) {
					lcsBuilder.appenddiffB(listY.get(yi));
				}
			}
			int [] PK = new int[2];//PK[0] P PK[1]K
			int[] prevPK = getPrevPK(p, k, oper, PK);
			p = prevPK[0];
			k = prevPK[1];
			
			while(xi>0) {
				cv = onpV[k];
				//System.out.println("p:" +p + " k:" + k + " cv is " + (cv==null?"null":"No null") );
				oper = cv.getOper(p);
				snakecnt = cv.getSnake(p);
				//System.out.println("x,y=" + (xi) + "," + (yi) + " oper:" + oper + " V" + p + "(" + (k - offset) + ") snake=" + snakecnt);
				for(int scnt = snakecnt ; scnt>0; scnt--) {
					
					/*{
						int oldx = xi;
						int oldy = yi;
						System.out.println(oldx + "," + oldy + "->" + (xi-1) + "," + (yi-1));
					}*/
					xi--;yi--;
					if(xi>=0&&xi<M) {
						if(lcsBuilder!=null) {
							//lcsBuilder.add(0,listX.get(xsn));//disasterous performance problem O(n)
							lcsBuilder.append(listX.get(xi));// value stored reverse order
						}
						lcscnt++;
					}
				}
				if(oper==1) {
					/*{
						int oldx = xi;
						int oldy = yi;
						System.out.println(oldx + "," + oldy + "->" + (xi-1) + "," + yi);
					}*/
					xi--;
					if(xi>=0&&xi<M&&lcsBuilder!=null) {
						lcsBuilder.appenddiffA(listX.get(xi));
					}
				}else if(oper==-1) {
					/*{
						int oldx = xi;
						int oldy = yi;
						System.out.println(oldx + "," + oldy + "->" + xi + "," + (yi-1));
					}*/
					yi--;
					if(yi>=0&&yi<N&&lcsBuilder!=null) {
						lcsBuilder.appenddiffB(listY.get(yi));
					}
				}
				
				if(xi==0){
					//System.out.println("xi,yi:" + xi + "," + yi);
					if(lcsBuilder!=null) {
						while(yi-->0) {
							lcsBuilder.appenddiffB(listY.get(yi));
							
						}
					}
					break;
				}
				
				prevPK = getPrevPK(p, k, oper, PK);
				p = prevPK[0];
				k = prevPK[1];
				
			}
		}
		//long getlcstotaltime = System.currentTimeMillis() - startTime;
		//System.out.println("getlcs time:" + getlcstotaltime);
		return lcscnt;
		
	}
	
	public int[] getPrevPK(int p, int k, int oper, int[]PK) {
		PK[0] = 0;
		PK[1] = 0;
		//int kstart = delta+offset;
		if(k==delta_offset) {
			PK[0] = p;
			switch(oper) {
			case 1:
				PK[1] = k+1;
				break;
			case -1:
				PK[1] = k-1;
				break;
			default:
					break;
			}
		}else if(k>delta_offset) {
			switch(oper) {
			case 1:
				PK[0] = p;
				PK[1] = k+1;
				break;
			case -1:
				PK[0] = p-1;
				PK[1] = k-1;
				break;
			default:
					break;
			}
		}else {//k<delta
			switch(oper) {
			case 1:
				PK[0] = p-1;
				PK[1] = k+1;
				break;
			case -1:
				PK[0] = p;
				PK[1] = k-1;
				break;
			default:
				break;
			}
		}
		//System.out.println("getPrevOK P[" + p + "->" + PK[0] + "] k[" + k + "->" + PK[1] + "] oper:" + oper);
		return PK;
	}
}
