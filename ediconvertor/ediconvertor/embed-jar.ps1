# Read the JAR file as bytes and convert to base64
$jarPath = "target\ediconvertor-0.0.1-SNAPSHOT.jar"
$jarBytes = [System.IO.File]::ReadAllBytes($jarPath)
$jarBase64 = [Convert]::ToBase64String($jarBytes)

# Create the C# code that will contain the embedded JAR
$code = @"
using System;
using System.IO;
using System.Diagnostics;

public class Program {
    // The JAR file content is embedded here as base64
    private static readonly string JarContent = @"$jarBase64";

    public static void Main() {
        try {
            Console.WriteLine("EDI Converter Service - Starting...");
            
            // Create directories
            Directory.CreateDirectory(@"C:\txt");
            Directory.CreateDirectory(@"C:\test");
            
            // Extract JAR to temp directory
            string tempDir = Path.Combine(Path.GetTempPath(), "edi-service");
            Directory.CreateDirectory(tempDir);
            string jarPath = Path.Combine(tempDir, "service.jar");
            
            // Convert base64 back to bytes and save as JAR
            byte[] jarBytes = Convert.FromBase64String(JarContent);
            File.WriteAllBytes(jarPath, jarBytes);
            
            // Start the Java process
            ProcessStartInfo psi = new ProcessStartInfo();
            psi.FileName = "java";
            psi.Arguments = $"-jar \"{jarPath}\" --server.port=8085";
            psi.UseShellExecute = false;
            psi.RedirectStandardOutput = true;
            psi.RedirectStandardError = true;
            
            Console.WriteLine("Starting service...");
            
            Process proc = new Process();
            proc.StartInfo = psi;
            proc.OutputDataReceived += (s, e) => { 
                if (e.Data != null) Console.WriteLine(e.Data); 
            };
            proc.ErrorDataReceived += (s, e) => { 
                if (e.Data != null) Console.WriteLine(e.Data); 
            };
            
            proc.Start();
            proc.BeginOutputReadLine();
            proc.BeginErrorReadLine();
            
            Console.WriteLine("\n=== EDI Converter Service ===");
            Console.WriteLine("Service URL: http://localhost:8085");
            Console.WriteLine("Input Directory: C:\\txt");
            Console.WriteLine("Output Directory: C:\\test");
            Console.WriteLine("=========================");
            Console.WriteLine("\nPress Ctrl+C to stop the service");
            
            proc.WaitForExit();
        }
        catch (Exception ex) {
            if (ex.Message.Contains("java")) {
                Console.WriteLine("\nError: Java is not installed or not in PATH");
                Console.WriteLine("Please install Java 17 or later from:");
                Console.WriteLine("https://www.oracle.com/java/technologies/downloads/");
            } else {
                Console.WriteLine($"\nError: {ex.Message}");
            }
            Console.WriteLine("\nPress any key to exit...");
            Console.ReadKey();
        }
    }
}
"@

# Create the executable
Add-Type -TypeDefinition $code -OutputAssembly "EDIConverter.exe" -OutputType ConsoleApplication
