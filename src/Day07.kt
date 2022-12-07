fun main() {
    // Returns root directory
    fun setupFileSystem(input: List<String>): Directory {
        // Setup root
        val root = Directory("/", null)
        var currentDir = root

        // Run through commands
        for(line in input) {
            val split = line.split(' ')
            if (split[0] == "$" && split[1] == "cd") {
                // Change directory
                currentDir = when(split[2]) {
                    "/" -> root
                    ".." -> currentDir.parentDirectory!!
                    else -> currentDir.subDirectories.find { it.name == split[2] }!!
                }
            } else if (split[0] == "$" && split[1] == "ls") {
                // List current directory
                // (Ignore -- we assume any non-commands are part of 'ls' results)
            } else if (split[0] == "dir") {
                // Dir in current directory
                currentDir.subDirectories.add(Directory(split[1], currentDir))
            } else {
                // File in current directory
                currentDir.files.add(File(split[1], split[0].toInt()))
            }
        }

        return root
    }

    fun part1(input: List<String>): Int {
        val root = setupFileSystem(input)

        // Calculate sizes
        val allSizes = mutableListOf<Int>()
        root.getSize(allSizes)

        // Total all less than or equal to 100000
        return allSizes.filter { it <= 100000 }.sum()
    }

    fun part2(input: List<String>): Int {
        val root = setupFileSystem(input)

        // Calculate sizes
        val allSizes = mutableListOf<Int>()
        root.getSize(allSizes)

        // Get amount needed to free up
        allSizes.sortDescending()
        val minFree = 30000000 - (70000000 - allSizes[0])

        // Get smallest above minFree
        return allSizes.last { it >= minFree }
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}

data class Directory(
    val name: String,
    val parentDirectory: Directory?
) {
    val files = mutableListOf<File>()
    val subDirectories = mutableListOf<Directory>()

    fun getSize(allSizes: MutableList<Int>): Int {
        val fileSizes = files.sumOf { it.size }
        val dirSizes = subDirectories.sumOf { it.getSize(allSizes) }
        val totalSize = fileSizes + dirSizes
        allSizes.add(totalSize)
        return totalSize
    }
}

data class File(
    val name: String,
    val size: Int
)