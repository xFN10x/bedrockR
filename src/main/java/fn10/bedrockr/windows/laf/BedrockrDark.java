package fn10.bedrockr.windows.laf;

import java.awt.Color;

import com.formdev.flatlaf.FlatDarkLaf;

public class BedrockrDark
	extends FlatDarkLaf
{
	public static final String NAME = "BedrockrDark";
	public static final Color BEDROCKR_GREEN = new Color(76, 255, 0);

	public static boolean setup() {
		return setup( new BedrockrDark() );
	}

	public static void installLafInfo() {
		installLafInfo( NAME, BedrockrDark.class );
	}

	@Override
	public String getName() {
		return NAME;
		
	}
}
