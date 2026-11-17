package fn10.bedrockr.installer.laf;

import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.*;

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
