package dtp.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class ExtensionFilter extends FileFilter {
	
	private String[] extensions;
	
	public ExtensionFilter(String[] extensions) {
		this.extensions = extensions;
	}
	
	/** {@inheritDoc} */
    public boolean accept(File file) {
        if (file.isDirectory())
            return true;
    
        String extension = getExtension(file);
        
        for (String ext : extensions) {
        	if (ext.equals(extension)) 
        		return true;
        }
        
        return false;
    }
    
    /**
     * 
     * @param file
     * @return the extension of given file
     */
    private static String getExtension(File file) {
        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    /** {@inheritDoc} */
    public String getDescription() {
    	StringBuilder builder = new StringBuilder("Extensions: ");
    	for (String ext : extensions) { 
    		builder.append(ext);
    		builder.append(" ");
    	}
        return builder.toString();
    }
}
