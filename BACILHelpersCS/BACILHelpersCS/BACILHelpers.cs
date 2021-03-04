using System;
using System.Diagnostics;

namespace BACILHelpers
{

    

    public class BACILConsole
    {
        public static void Write(Object value)
        {
            Console.Write(value);
        }
    }

    public class BACILEnvironment
    {
        static Stopwatch stopWatch = new Stopwatch();

        public static void StartTimer()
        {
            stopWatch.Restart();
        }

        public static long GetTicks()
        {
            stopWatch.Stop();
            return stopWatch.ElapsedTicks;
        }


    }
}
