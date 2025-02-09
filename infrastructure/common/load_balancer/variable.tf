variable "server_ami" {

}

variable "server_instance_type" {

}

variable "app_region" {

}

variable "aws_security_group_id" {
  description = "Security group ID for EC2"
}

variable "aws_key_pair_name" {
  description = "Key Pair Name for EC2"
}

variable "servers" {
  description = "List of public IPs for EC2 instances"
  type        = list(string)
}

