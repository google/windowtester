/*
 * Created on Jul 19, 2004
 */
package com.realpersist.gef.editor;

import com.realpersist.gef.model.Column;
import com.realpersist.gef.model.ColumnType;
import com.realpersist.gef.model.Relationship;
import com.realpersist.gef.model.Schema;
import com.realpersist.gef.model.Table;


/**
 * Creates some arbitrary default content for the editor
 * @author Phil Zoio
 */
public class ContentCreator
{
	/**
	 * Returns the content of this editor
	 * 
	 * @return the model object
	 */
	public Schema getContent()
	{

		// todo return your model here
		Schema schema = new Schema("Test schema");

		Table t1 = new Table("DEPT", schema);
		Column t1c1 = new Column("DEPT_NAME", ColumnType.VARCHAR);
		Column t1c2 = new Column("DEPT_ID", ColumnType.INTEGER);
		t1.addColumn(t1c1);
		t1.addColumn(t1c2);

		Table t2 = new Table("EMPLOYEE", schema);
		Column t2c1 = new Column("EMPLOYEE_NAME", ColumnType.VARCHAR);
		Column t2c2 = new Column("EMPLOYEE_ID", ColumnType.INTEGER);
		Column t2c3 = new Column("SECRET_HABIT", ColumnType.VARCHAR);

		t2.addColumn(t2c1);
		t2.addColumn(t2c2);
		t2.addColumn(t2c3);

		Table t3 = new Table("LOGIN", schema);
		Column t3c1 = new Column("USER_NAME", ColumnType.VARCHAR);
		Column t3c2 = new Column("USER_PASSWORD", ColumnType.VARCHAR);
		Column t3c3 = new Column("LEVEL", ColumnType.INTEGER);
		t3.addColumn(t3c1);
		t3.addColumn(t3c2);
		t3.addColumn(t3c3);

		Relationship relationshipT1T2 = new Relationship(t2, t1);
		Relationship relationshipT1T3 = new Relationship(t3, t1);
		Relationship relationshipT2T3 = new Relationship(t3, t2);

		Table t4 = new Table("BINS", schema);
		Column t4c1 = new Column("BIN_LOCATION", ColumnType.INTEGER);
		t4.addColumn(t4c1);

		Table t5 = new Table("WATER_COOLERS", schema);
		Column t5c1 = new Column("WATER_COOLER_ID", ColumnType.INTEGER);
		Column t5c2 = new Column("COOLER_LOCATION", ColumnType.VARCHAR);
		t5.addColumn(t5c1);
		t5.addColumn(t5c2);

		Table t6 = new Table("DESK", schema);
		Column t6c1 = new Column("DESK_LOCATION", ColumnType.VARCHAR);
		Column t6c2 = new Column("DESK_ID", ColumnType.INTEGER);
		t6.addColumn(t6c1);
		t6.addColumn(t6c2);
		Table t7 = new Table("COMPUTER", schema);
		Column t7c1 = new Column("COMPUTER_NAME", ColumnType.VARCHAR);
		Column t7c2 = new Column("COMPUTER_ID", ColumnType.INTEGER);
		t7.addColumn(t7c1);
		t7.addColumn(t7c2);

		Relationship relationshipT6T3 = new Relationship(t6, t2);
		Relationship relationshipT7T1 = new Relationship(t7, t1);
		Relationship relationshipT3T7 = new Relationship(t3, t7);
		Relationship relationshipT6T7 = new Relationship(t3, t6);

		//just to be awkward - we'll have a FK relationship from T6 to T1
		Relationship relationshipT1T6 = new Relationship(t1, t6);

		/** ** now build some independent related clusters *** */

		Table c1 = new Table("CITY", schema);
		Column c1c1 = new Column("NUMBER_OF_EMPLOYEES", ColumnType.INTEGER);
		Column c1c2 = new Column("CITY_NAME", ColumnType.VARCHAR);
		c1.addColumn(c1c1);
		c1.addColumn(c1c2);

		Table c2 = new Table("COUNTRY", schema);
		Column c2c1 = new Column("NUMBER_OF_BRANCHES", ColumnType.INTEGER);
		Column c2c2 = new Column("COUNTRY_NAME", ColumnType.VARCHAR);
		c2.addColumn(c2c1);
		c2.addColumn(c2c2);

		Table c3 = new Table("RESULTS", schema);
		Column c3c1 = new Column("NET_INCOME", ColumnType.INTEGER);
		Column c3c3 = new Column("COMMENTS", ColumnType.VARCHAR);
		c3.addColumn(c3c1);
		c3.addColumn(c3c3);

		Table c4 = new Table("QUARTERS", schema);
		Column c4c1 = new Column("QUARTER_NUMBER", ColumnType.INTEGER);
		c4.addColumn(c4c1);

		Relationship relationshipC1C2 = new Relationship(c1, c2);
		Relationship relationshipC3C4 = new Relationship(c3, c4);

		schema.addTable(t1);
		schema.addTable(t2);
		schema.addTable(t3);
		schema.addTable(t4);
		schema.addTable(t5);
		schema.addTable(t6);
		schema.addTable(t7);

		schema.addTable(c1);
		schema.addTable(c2);
		schema.addTable(c3);
		schema.addTable(c4);

		return schema;

	}

}
