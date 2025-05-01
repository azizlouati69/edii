# Read the JAR file and convert it to base64
$jarPath = "target\ediconvertor-0.0.1-SNAPSHOT.jar"
$jarBytes = [System.IO.File]::ReadAllBytes($jarPath)
$jarBase64 = [Convert]::ToBase64String($jarBytes)

$code = @"
using System;
using System.IO;
using System.Diagnostics;

public class Program {
    private static readonly string JarContent = @"$jarBase64";

    public static void Main() {
        try {
            Console.WriteLine("EDI Service - Starting...");
            
            // Create directories
            Directory.CreateDirectory(@"C:\txt");
            Directory.CreateDirectory(@"C:\test");
            
            // Extract JAR
            string tempDir = Path.Combine(Path.GetTempPath(), "edi-service");
            Directory.CreateDirectory(tempDir);
            string jarPath = Path.Combine(tempDir, "app.jar");
            File.WriteAllBytes(jarPath, Convert.FromBase64String(JarContent));
            
            // Run the service
            var psi = new ProcessStartInfo {
                FileName = "java",
                Arguments = $"-jar \"{jarPath}\" --server.port=8085",
                UseShellExecute = false,
                RedirectStandardOutput = true,
                RedirectStandardError = true
            };
            
            var proc = Process.Start(psi);
            Console.WriteLine("Service started on port 8085");
            Console.WriteLine("Input: C:\\txt");
            Console.WriteLine("Output: C:\\test");
            Console.WriteLine("\nPress Enter to exit");
            Console.ReadLine();
            
            try { proc.Kill(); } catch { }
        }
        catch (Exception ex) {
            Console.WriteLine($"Error: {ex.Message}");
            Console.ReadLine();
        }
    }
}
"@

Add-Type -TypeDefinition $code -OutputAssembly "EDIService.exe" -OutputType ConsoleApplication
Write-Host "Created EDIService.exe successfully!"
