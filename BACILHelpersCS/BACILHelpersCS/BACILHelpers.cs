using System;

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
        public static long TickCount()
        {
            return Environment.TickCount;
        }
    }
}
