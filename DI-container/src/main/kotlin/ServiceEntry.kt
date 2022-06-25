class ServiceEntry(
    var service: Any?,
    var factory: ((ResolverInterface) -> Any)?,
    var scope: Scope,
    var isResolving: Boolean = false
) {}