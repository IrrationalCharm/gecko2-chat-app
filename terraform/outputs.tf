#This file defines values you want Terraform to print out to the console after a successful run
# (for example, the public IP address of a newly created server).

output "instance_public_ip" {
  value       = ""                                          # The actual value to be outputted
  description = "The public IP address of the EC2 instance" # Description of what this output represents
}
