package dalcoms.pub.fingerbrickbreaker.level;

import com.google.gson.annotations.SerializedName;

public class JsonDataLevelPreferences {
	@SerializedName("guideOn")
	private String guideOn;
	
	public boolean isBulletTopAttach(){
		return Boolean.valueOf(this.guideOn);
	}

}
