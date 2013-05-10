package cn.edu.xidian.robot.string;

public class StringProcesser {
	 private static final char[] USELESS_CHAR= {',','.','?','，','。','？','哦','噢','啊','嗯'};
	
	 private static final int MAXLEN=500;
	 private static int[][] cmpBuf=new int[MAXLEN][MAXLEN];
	 
	 public static String getOffSuffixe(String str){
	       String re="";
	       for(int i = 0 ; i < str.length() ; i++ ){
	    	   if(str.charAt(i) == '.') break;
	    	   re += str.charAt(i);
	       }
	       return re;
    }
	 
	//对识别结果进行处理，剔除其中的标点、嗯、哦等误识别的无意义信息
	public static String pureContent(String sourceString){
		   String result = "";
		   for(int i = 0 ; i< sourceString.length();i++){
			   char c = sourceString.charAt(i);
               boolean flag = true;
               for(int j = 0;j < USELESS_CHAR.length ;j++ ){
            	   if(c == USELESS_CHAR[j]) {flag = false; break;}
               }
               if(flag) result += c;
		   }
		   return result;
	}
	public static double match(String cmpStr,String dstStr){
	     int cmpLen=cmpStr.length();
	     int dstLen=dstStr.length();
	     int minLen=dstLen;
	     if(dstLen>cmpLen) minLen=cmpLen;//取最小值
	     if(cmpLen>=MAXLEN) cmpLen=MAXLEN-1;
	     if(dstLen>=MAXLEN) dstLen=MAXLEN-1;
	     for(int i=0;i<=cmpLen;i++)
		   for(int j=0;j<=dstLen;j++){
			         if(0==i || 0==j) {cmpBuf[i][j]=0;continue;}
			         if(cmpStr.charAt(i-1)==dstStr.charAt(j-1)) {cmpBuf[i][j]=cmpBuf[i-1][j-1]+1;continue;}
			         cmpBuf[i][j]=(cmpBuf[i-1][j]>cmpBuf[i][j-1])?cmpBuf[i-1][j]:cmpBuf[i][j-1];
		   }
	     if(0==dstLen || 0==cmpLen) return 0.0;
	     double re=((double)cmpBuf[cmpLen][dstLen])/((double)minLen);
	     return re;
}
}
