output "security_group_id" {
  value = aws_security_group.allow_tcp_and_ssh.id
}