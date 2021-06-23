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
        static long nanosecPerTick = (1000L * 1000L * 1000L) / Stopwatch.Frequency;

        public static void StartTimer()
        {
            stopWatch.Restart();
        }

        public static long GetTicks()
        {
            stopWatch.Stop();
            return stopWatch.ElapsedTicks * nanosecPerTick;
        }


    }
}
