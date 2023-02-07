package mobi.waterdog.rest.template.tests.core.model

enum class RepairStatus(val status: String) {
    Created("Created"),
    InProgress("InProgress"),
    Complete("Complete");

    override fun toString(): String = this.status
}