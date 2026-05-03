package fn10.bedrockr.windows.laf;

import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.*;

public class BedrockrLight
	extends FlatDarkLaf
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
