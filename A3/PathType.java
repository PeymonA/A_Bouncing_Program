/*
 *	===============================================================================
 *	PathType represents the type of a path
 *  YOUR UPI: PARM175
 *	=============================================================================== */

enum PathType { BOUNCE, FALL;
	public PathType next() {
		return values()[(ordinal() + 1) % values().length];
	}
}
