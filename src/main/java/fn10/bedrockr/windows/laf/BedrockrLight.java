package fn10.bedrockr.windows.laf;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import java.awt.*;

public class BedrockrLight
	extends FlatLightLaf
{
	public static final String NAME = "BedrockrLight";
	public static final Color BEDROCKR_GREEN = new Color(76, 255, 0);

	public static boolean setup() {
		return setup( new BedrockrLight() );
	}

	public static void installLafInfo() {
		installLafInfo( NAME, BedrockrLight.class );
	}

	@Override
	public String getName() {
		return NAME;
	}
}
