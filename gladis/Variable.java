package gladis;

import java.io.Serializable;

public class Variable implements Serializable{
	int max;
	int min;
	int val;
	String var;
	
	public Variable(String var) {
		this.var=var;
	}
	
	public void setMax(int max) {
		this.max = max;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	public String getVar() {
		return var;
	}

	public int getVal() {
		return val;
	}
	public void setVal(int val) {
		if(val>max) return;
		if (val<min) return;
		this.val = val;
	}
	
}
