output "server_ips" {
  description = "Public IP addresses of the created EC2 instances"
  value       = aws_instance.server[*].public_ip
}