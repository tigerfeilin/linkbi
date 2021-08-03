
package com.linkbi.datax.db.constant;

/**
 * 数据库表类型:视图表、物理表
 * 
 * @author
 *
 */
public enum DBTableType {
	/**
	 * 物理表
	 */
	TABLE(0),
	
	/**
	 * 视图表
	 */
	VIEW(1);

	private int index;

	DBTableType(int idx) {
		this.index = idx;
	}

	public int getIndex() {
		return index;
	}
}
