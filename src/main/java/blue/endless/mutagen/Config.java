package blue.endless.mutagen;

public class Config {
	public static Config INSTANCE = null;
	
	public Mutants mutants = new Mutants();
	
	public class Mutants {
		int idleTicksBeforeDespawn = -1;
	}
}
