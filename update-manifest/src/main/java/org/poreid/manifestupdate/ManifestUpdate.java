/*
 * The MIT License
 *
 * Copyright 2014 Rui Martinho (rmartinho@gmail.com), António Braz (antoniocbraz@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.poreid.manifestupdate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 *
 * @author rmartinho@gmail.com
 */

@Mojo(name = "update-manifest", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class ManifestUpdate extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.directory}", property = "archiveDirectory", required = true)
    private File archiveDirectory;
    
    @Parameter(alias = "includes", property = "includes", required = true)
    private String[] mIncludes;
    
    @Parameter(alias = "excludes", property = "excludes")
    private String[] mExcludes;
    
    @Parameter(property = "verbose", defaultValue="false")
    private boolean verbose;
    
    @Parameter(alias = "Codebase", property = "Codebase")
    private String codebase;
    
    @Parameter(alias = "Trusted-Library", property = "Trusted-Library", defaultValue="false")
    private boolean trustedlib;
    
    @Parameter(alias = "Caller-Allowable-Codebase", property = "Caller-Allowable-Codebase")
    private String callerAllowableCodebase;
    
    @Parameter(alias = "Permissions", property = "Permissions")
    private String permissions;
    
    @Parameter(alias = "Application-Library-Allowable-Codebase", property = "Application-Library-Allowable-Codebase")
    private String appLibAllCodeBase;
    
    
    private String executable;
    private File mfTemp;

    public ManifestUpdate() {}

   
    public void execute() throws MojoExecutionException, MojoFailureException {
        String codeBase = (null!=this.codebase && !this.codebase.isEmpty()) ? String.format("Codebase: %s\n", this.codebase) : "";
        String trustedLib = (trustedlib) ? String.format("Trusted-Library: %s\n", this.trustedlib) : "";
        String permissions_ = (null!=this.permissions && !this.permissions.isEmpty()) ? String.format("Permissions: %s\n", this.permissions) : "";
        String callerAC = (null!=this.callerAllowableCodebase && !this.callerAllowableCodebase.isEmpty()) ? String.format("Caller-Allowable-Codebase: %s\n", callerAllowableCodebase) : "";
        String alac = (null!=this.appLibAllCodeBase && !this.appLibAllCodeBase.isEmpty()) ? String.format("Application-Library-Allowable-Codebase: %s\n", appLibAllCodeBase) : "";
        
        
        String includeList = ( mIncludes != null ) ? StringUtils.join( mIncludes, "," ) : null;
        String excludeList = ( mExcludes != null ) ? StringUtils.join( mExcludes, "," ) : null;
        List<File> jarFiles;

        executable = getExecutable();
        
        try {
            jarFiles = FileUtils.getFiles(archiveDirectory, includeList, excludeList);
            FileUtils.mkdir(archiveDirectory.getAbsolutePath()+File.separator+"META-INF");
            mfTemp = new File(archiveDirectory.getAbsolutePath()+File.separator+"META-INF", "MANIFEST.MF");       
            mfTemp.deleteOnExit();
            FileUtils.fileWrite(mfTemp, "UTF-8", codeBase+trustedLib+permissions_+callerAC+alac);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to scan archive directory for JARs: "+ e.getMessage(), e);
        }
        
        for (File file : jarFiles) {
            processArchive(file);
        }

        getLog().info("Properties: "+codeBase+trustedLib+permissions_+callerAC+alac);
        getLog().info(jarFiles.size() + " archive(s) processed (" + archiveDirectory.toString() + ")");

        try {
            FileUtils.deleteDirectory(archiveDirectory + File.separator + "META-INF");
        } catch (IOException ex) {
            throw new MojoExecutionException("Failed to delete META-INF directory: " + ex.getMessage(), ex);
        }
    }
    
    
    private void processArchive(final File archive) throws MojoExecutionException {
        
        if (archive == null) {
            throw new NullPointerException("archive");
        }
        
        if (this.verbose) {
            getLog().info(getMessage("processing", archive.getAbsolutePath()));
        } else if (getLog().isDebugEnabled()) {
            getLog().debug(getMessage("processing", archive.getAbsolutePath()));
        }
        
        Commandline addManisfestToJarCommand = new Commandline();
        addManisfestToJarCommand.setExecutable(this.executable);
        addManisfestToJarCommand.setWorkingDirectory(archiveDirectory);
        addManisfestToJarCommand.createArg().setValue("-umf");        
        addManisfestToJarCommand.createArg().setFile(this.mfTemp);
        addManisfestToJarCommand.createArg().setFile(archive);
      
        try {
            if (getLog().isDebugEnabled()) {
                getLog().debug(getMessage("command", addManisfestToJarCommand.toString()));
            }

            final int result = CommandLineUtils.executeCommandLine(addManisfestToJarCommand,
                    new InputStream() {
                public int read() {
                    return -1;
                }
            }, new StreamConsumer() {
                public void consumeLine(final String line) {
                    if (verbose) {
                        getLog().info(line);
                    } else {
                        getLog().debug(line);
                    }
                }
            }, new StreamConsumer() {
                public void consumeLine(final String line) {
                    getLog().warn(line);
                }
            });

            if (result != 0) {
                throw new MojoExecutionException(getMessage("failure", addManisfestToJarCommand.toString()));
            }
        } catch (CommandLineException e) {
            throw new MojoExecutionException(getMessage("commandLineException", addManisfestToJarCommand.toString()), e);
        }
    }
    
    
    private String getExecutable() {
        String command = "jar" + (Os.isFamily(Os.FAMILY_WINDOWS) ? ".exe" : "");

        String exec = findExecutable(command, System.getProperty("java.home"), new String[]{"../bin", "bin", "../sh"});

        if (exec == null) {
            try {
                Properties env = CommandLineUtils.getSystemEnvVars();

                String[] variables = {"JDK_HOME", "JAVA_HOME"};

                for (int i = 0; i < variables.length && exec == null; i++) {
                    exec = findExecutable(command, env.getProperty(variables[i]), new String[]{"bin", "sh"});
                }
            } catch (IOException e) {
                if (getLog().isDebugEnabled()) {
                    getLog().warn("Failed to retrieve environment variables, cannot search for " + command, e);
                } else {
                    getLog().warn("Failed to retrieve environment variables, cannot search for " + command);
                }
            }
        }

        if (exec == null) {
            exec = command;
        }

        return exec;
    }
    
    
    private String findExecutable(String command, String homeDir, String[] subDirs) {
        if (StringUtils.isNotEmpty(homeDir)) {
            for (String subDir : subDirs) {
                File file = new File(new File(homeDir, subDir), command);
                if (file.isFile()) {
                    return file.getAbsolutePath();
                }
            }
        }

        return null;
    }
    
    
    private String getMessage(String info, String msg){
        return info + " --- " + msg;
    }
}
