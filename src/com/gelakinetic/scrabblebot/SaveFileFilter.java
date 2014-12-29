package com.gelakinetic.scrabblebot;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class SaveFileFilter extends FileFilter {

	@Override
	public boolean accept(File arg0) {

		if(arg0.isDirectory()) {
			return true;
		}
		
		String ext = null;
		String s = arg0.getName();
		int i = s.lastIndexOf('.');
		
		if (i > 0 &&  i < s.length() - 1) {
		    ext = s.substring(i+1).toLowerCase();
		}
		if(ext == null) {
			return false;
		}
		return ext.equalsIgnoreCase("sav");
	}

	@Override
	public String getDescription() {
		return ".sav files";
	}

}
