package y2023

import Direction
import getAt
import getSurrounding
import java.util.PriorityQueue

class Day17(val input: List<String>) {
    fun part1(): Int {
        val map = parseInput(input)

        val end = map.getAt(map[0].size - 1, map.size - 1)!!

        val bestRoute = findRoutes(map, end)
        printRoute(map, bestRoute)

        return bestRoute.cost
    }

    private fun printRoute(map: Array<Array<CityBlock>>, route: Route) {
        map.forEachIndexed { y, row ->
            row.forEachIndexed { x, cityBlock ->
                if (route.visited.contains(cityBlock)) {
                    print("#")
                } else {
                    print(cityBlock.cost)
                }
            }
            println()
        }
    }

    fun part2(): Int {
        return 2
    }

    private fun findRoutes(map: Array<Array<CityBlock>>, target: CityBlock): Route {
        val routeQueue: PriorityQueue<Route> = PriorityQueue()
        val startPoint = map.getAt(0, 0)!!
        val startRoute = Route(startPoint, 0, listOf())
        routeQueue.add(startRoute)
        var lowestCost = Int.MAX_VALUE
        var bestRoute: Route? = null

        while (routeQueue.isNotEmpty()) {
            val currentRoute = routeQueue.poll()
            val currentPosition = currentRoute.position

            if (currentPosition == target && currentRoute.cost < lowestCost) {
                bestRoute = currentRoute
                lowestCost = lowestCost.coerceAtMost(currentRoute.cost)
                continue
            }

            if (currentRoute.cost > lowestCost) {
                continue
            }

            val allowedDirections = currentRoute.getAllowedDirections()

            map.getSurrounding(currentPosition.x, currentPosition.y, allowedDirections)
                    .filterNot { currentRoute.visited.contains(it) }
                    .sortedBy { it.cost }
                    .map { targetBlock -> Route(targetBlock, currentRoute.cost + targetBlock.cost, currentRoute.visited + targetBlock) }
                    .filterNot { queueContainsBetterRouteToPoint(routeQueue, it) }
                    .forEach { routeQueue.add(it) }
        }

        return bestRoute!!
    }

    private fun queueContainsBetterRouteToPoint(routeQueue: PriorityQueue<Route>, route: Route): Boolean {
        return routeQueue.any { it.position == route.position && it.cost < route.cost && it.getAllowedDirections() == route.getAllowedDirections() }
//    return routeQueue.any { it.position == route.position && it.visited.size <= route.visited.size && it.cost < route.cost }
//    return routeQueue.any { it.position == route.position && it.cost * 2 < route.cost }
//    return false
    }

    private fun parseInput(input: List<String>): Array<Array<CityBlock>> {
        return input.mapIndexed { y, line ->
            line.mapIndexed { x, char ->
                CityBlock(x, y, char.digitToInt())
            }.toTypedArray()
        }.toTypedArray()
    }

    private data class Route(var position: CityBlock, var cost: Int, var visited: List<CityBlock>) : Comparable<Route> {
        fun getAllowedDirections(): Set<Direction> {
            if (visited.size < 3) {
                return setOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)
            }

            val (first, second, third) = visited.takeLast(3)

            if (first.x == second.x && second.x == third.x) {
                return setOf(Direction.LEFT, Direction.RIGHT)
            }

            if (first.y == second.y && second.y == third.y) {
                return setOf(Direction.UP, Direction.DOWN)
            }

            // don't travel back
            if (second.x == third.x && second.y < third.y) {
                // we moved down
                return setOf(Direction.DOWN, Direction.LEFT, Direction.RIGHT)
            }

            if (second.x == third.x && second.y > third.y) {
                // we moved up
                return setOf(Direction.UP, Direction.LEFT, Direction.RIGHT)
            }

            if (second.y == third.y && second.x < third.x) {
                // we moved right
                return setOf(Direction.UP, Direction.DOWN, Direction.RIGHT)
            }

            if (second.y == third.y && second.x > third.x) {
                // we moved left
                return setOf(Direction.UP, Direction.DOWN, Direction.LEFT)
            }

            error("Unreachable code")
        }

        override fun compareTo(other: Route): Int {
            return visited.size.compareTo(other.visited.size)
        }
    }

    private data class CityBlock(val x: Int, val y: Int, val cost: Int)
}