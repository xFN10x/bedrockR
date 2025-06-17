package fn10.bedrockr.windows.laf;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.icons.FlatWindowCloseIcon;

public class BedrockrDark
	extends FlatDarkLaf
{
	public static final String NAME = "bedrockrDark";

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
