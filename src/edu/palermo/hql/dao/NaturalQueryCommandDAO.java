package edu.palermo.hql.dao;

import edu.palermo.hql.bo.*;
import java.util.*;

public interface NaturalQueryCommandDAO {

	public List<NaturalQueryCommand> getNaturalQueryCommands();
	public List<NaturalQueryCommand> getNaturalQueryCommands(String type);
	public NaturalQueryCommand getNaturalQueryCommandByName(String name);
}
