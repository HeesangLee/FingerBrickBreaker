package dalcoms.pub.fingerbrickbreaker.level;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class JsonDataLevelData {
	@SerializedName("level")
	private ArrayList<JsonDataLevel> levels;


	public ArrayList<JsonDataLevel> getLevels() {
		return this.levels;
	}

}
