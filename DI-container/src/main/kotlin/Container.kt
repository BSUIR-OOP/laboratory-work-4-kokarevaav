interface ContainerInterface {
    fun <T> register(
        service: T,
        scope: Scope,
        factory: (ResolverInterface) -> T
    )
}

class Container: ContainerInterface, ResolverInterface {
    companion object {
        val instance = Container()
    }

    private var services = HashMap<String, ServiceEntry>()

    override fun <T> register(
        service: T,
        scope: Scope,
        factory: (ResolverInterface) -> T
    ) {
        val serviceName = resolveName(service)
        if (services.keys.contains(serviceName)) {
            return
        }

        val serviceEntry = if (scope == Scope.Singleton) {
            ServiceEntry(
                factory(this),
                null,
                scope
            )
        } else {
             ServiceEntry(
                null,
                factory,
                scope
            )
        }
        services[serviceName] = serviceEntry
    }

    override fun <T> resolve(): T {
        val serviceName = resolveName(T::class)
        val serviceEntry = services[serviceName] ?: throw ServiceNotRegistered()
        if (serviceEntry.isResolving) throw CyclicDependency()
        if (serviceEntry.scope == Scope.Singleton) {
            val service = serviceEntry.service as T
            serviceEntry.isResolving = false
            return service
        }
        if (serviceEntry.scope == Scope.Transient) {
            val service = serviceEntry.factory?.invoke(this)
            serviceEntry.isResolving = false
            return service as T
        }
        throw UndefinedBehaviour()
    }

    private fun <T> resolveName(service: T): String {
        return service!!::class.simpleName!!
    }
}