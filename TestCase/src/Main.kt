class A
class B(val a: A)

class C(val d: D)
class D(val c: C)

fun main() {
    val container = Container.instance

    container.register(
        A,
        Scope.Singleton
    ) { A() }

    container.register(
        B,
        Scope.Transient
    ) { r ->
        B(r.resolve())
    }

    val b: B = container.resolve()

    // Throws Error (Service is not registered)
    container.register(
        C,
        Scope.Singleton
    ) { r ->
        C(r.resolve())
    }
}